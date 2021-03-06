/**
 * This function is called by Forge before any minecraft classes are loaded to
 * setup the coremod.
 *
 * @return {object} All the transformers of this coremod.
 */
function initializeCoreMod() {

    /*Class/Interface*/ Opcodes = Java.type("org.objectweb.asm.Opcodes");
    /*Class*/ ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");

    /*Class*/ InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    /*Class*/ LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");

    /*Class*/ FieldNode = Java.type("org.objectweb.asm.tree.FieldNode");
    /*Class*/ MethodNode = Java.type("org.objectweb.asm.tree.MethodNode");

    /*Class*/ AbstractInsnNode = Java.type("org.objectweb.asm.tree.AbstractInsnNode");
    /*Class*/ InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
    /*Class*/ VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
    /*Class*/ FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
    /*Class*/ MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
    /*Class*/ JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
    /*Class*/ TypeInsnNode = Java.type("org.objectweb.asm.tree.TypeInsnNode");

    ACC_PUBLIC = Opcodes.ACC_PUBLIC;

    INVOKESTATIC = Opcodes.INVOKESTATIC;
    INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;

    ALOAD = Opcodes.ALOAD;
    ILOAD = Opcodes.ILOAD;
    FLOAD = Opcodes.FLOAD;
    DLOAD = Opcodes.DLOAD;

    ASTORE = Opcodes.ASTORE;
    ISTORE = Opcodes.ISTORE;

    RETURN = Opcodes.RETURN;
    ARETURN = Opcodes.ARETURN;
    IRETURN = Opcodes.IRETURN;
    DRETURN = Opcodes.DRETURN;

    NEW = Opcodes.NEW;

    ACONST_NULL = Opcodes.ACONST_NULL;
    ICONST_0 = Opcodes.ICONST_0;

    IFEQ = Opcodes.IFEQ;
    IFNE = Opcodes.IFNE;
    IF_ACMPEQ = Opcodes.IF_ACMPEQ;
    IFNULL = Opcodes.IFNULL;

    GETFIELD = Opcodes.GETFIELD;
    GETSTATIC = Opcodes.GETSTATIC;

    GOTO = Opcodes.GOTO;

    LABEL = AbstractInsnNode.LABEL;
    METHOD_INSN = AbstractInsnNode.METHOD_INSN;
    VAR_INSN = AbstractInsnNode.VAR_INSN;

    isOptiFinePresent = false;

    return wrapWithLogging(wrapMethodTransformers({
        "CreateBabyVillagerTask#func_220477_a": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.entity.ai.brain.task.CreateBabyVillagerTask",
                "methodName": "func_220477_a",
                "methodDesc": "(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/entity/merchant/villager/VillagerEntity;Lnet/minecraft/util/math/BlockPos;)V"
            },
            "transformer": function(methodNode) {
                var instructions = methodNode.instructions;
                injectCreateVillagerBabyEventFire(instructions);
                return methodNode;
            }
        },
        "VillageStructure.Stack#init": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.world.gen.feature.structure.VillageStructure$Start",
                "methodName": "func_214625_a",
                "methodDesc": "(Lnet/minecraft/world/gen/ChunkGenerator;Lnet/minecraft/world/gen/feature/template/TemplateManager;IILnet/minecraft/world/biome/Biome;)V"
            },
            "transformer": function(methodNode) {
                var instructions = methodNode.instructions;
                injectFireVillageStructureStartEvent(instructions);
                return methodNode;
            }
        }
    }));
}

/*
  // access flags 0x2
  private func_220477_a(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/entity/merchant/villager/VillagerEntity;Lnet/minecraft/util/math/BlockPos;)V
   L0
    LINENUMBER 120 L0
    ALOAD 1
    INVOKEVIRTUAL net/minecraft/world/server/ServerWorld.getDimension ()Lnet/minecraft/world/dimension/Dimension;
    INVOKEVIRTUAL net/minecraft/world/dimension/Dimension.getType ()Lnet/minecraft/world/dimension/DimensionType;
    ALOAD 3
    INVOKESTATIC net/minecraft/util/math/GlobalPos.of (Lnet/minecraft/world/dimension/DimensionType;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/GlobalPos;
    ASTORE 4
   L1
    LINENUMBER 121 L1
    ALOAD 2
    INVOKEVIRTUAL net/minecraft/entity/merchant/villager/VillagerEntity.getBrain ()Lnet/minecraft/entity/ai/brain/Brain;
    GETSTATIC net/minecraft/entity/ai/brain/memory/MemoryModuleType.HOME : Lnet/minecraft/entity/ai/brain/memory/MemoryModuleType;
    ALOAD 4
    INVOKEVIRTUAL net/minecraft/entity/ai/brain/Brain.setMemory (Lnet/minecraft/entity/ai/brain/memory/MemoryModuleType;Ljava/lang/Object;)V
   L2
    ALOAD 1
    ALOAD 2
    ALOAD 3
    INVOKESTATIC ar/com/messupetru/expansivevillages/VillageManager.fireCreateVillagerBabyEvent (Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/entity/merchant/villager/VillagerEntity;Lnet/minecraft/util/math/GlobalPos;)V
   L3
    LINENUMBER 122 L2
    RETURN
   L4
    LOCALVARIABLE this Lnet/minecraft/entity/ai/brain/task/CreateBabyVillagerTask; L0 L3 0
    LOCALVARIABLE p_220477_1_ Lnet/minecraft/world/server/ServerWorld; L0 L3 1
    LOCALVARIABLE p_220477_2_ Lnet/minecraft/entity/merchant/villager/VillagerEntity; L0 L3 2
    LOCALVARIABLE p_220477_3_ Lnet/minecraft/util/math/BlockPos; L0 L3 3
    LOCALVARIABLE globalpos Lnet/minecraft/util/math/GlobalPos; L1 L3 4
    MAXSTACK = 3
    MAXLOCALS = 5

 */
function injectCreateVillagerBabyEventFire(instructions) {
    var penultimateLabel;
    var arrayLength = instructions.size();
    var lastIsPassed = false;

    for (var i = arrayLength-1; i >= 0; --i) {
        var instruction = instructions.get(i);
        if (instruction.getType() == LABEL) {
            if(!lastIsPassed) {
                lastIsPassed = true;
            } else {
                penultimateLabel = instruction;
                print("Found injection point \"penultimate Label\" " + instruction);
                break;
            }
        }
    }
    if (!penultimateLabel)
        throw "Error: Couldn't find injection point \"penultimate Label\"!";

    var toInject = new InsnList();

    // Labels n stuff
    var originalInstructionsLabel = new LabelNode();

    toInject.add(new VarInsnNode(ALOAD, 1));
    toInject.add(new VarInsnNode(ALOAD, 2));
    toInject.add(new VarInsnNode(ALOAD, 4));

    // Make list of instructions to inject
    toInject.add(new MethodInsnNode(
        //int opcode
        INVOKESTATIC,
        //String owner
        "ar/com/messupetru/expansivevillages/VillageManager",
        //String name
        "fireCreateVillagerBabyEvent",
        //String descriptor
        "(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/entity/merchant/villager/VillagerEntity;Lnet/minecraft/util/math/GlobalPos;)V",
        //boolean isInterface
        false
    ));

    toInject.add(originalInstructionsLabel);

    // Inject instructions
    instructions.insert(penultimateLabel, toInject);
}

/*
public init(Lnet/minecraft/world/gen/ChunkGenerator;Lnet/minecraft/world/gen/feature/template/TemplateManager;IILnet/minecraft/world/biome/Biome;)V
  L0
    LINENUMBER 61 L0
    ALOAD 1
    ALOAD 5
    GETSTATIC net/minecraft/world/gen/feature/Feature.VILLAGE : Lnet/minecraft/world/gen/feature/structure/Structure;
    INVOKEVIRTUAL net/minecraft/world/gen/ChunkGenerator.getStructureConfig (Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/feature/structure/Structure;)Lnet/minecraft/world/gen/feature/IFeatureConfig;
    CHECKCAST net/minecraft/world/gen/feature/structure/VillageConfig
    ASTORE 6
   L1
    LINENUMBER 62 L1
    NEW net/minecraft/util/math/BlockPos
    DUP
    ILOAD 3
    BIPUSH 16
    IMUL
    ICONST_0
    ILOAD 4
    BIPUSH 16
    IMUL
    INVOKESPECIAL net/minecraft/util/math/BlockPos.<init> (III)V
    ASTORE 7
   L2
    LINENUMBER 63 L2
    ALOAD 1
    ALOAD 2
    ALOAD 7
    ALOAD 0
    GETFIELD net/minecraft/world/gen/feature/structure/VillageStructure$Start.components : Ljava/util/List;
    ALOAD 0
    GETFIELD net/minecraft/world/gen/feature/structure/VillageStructure$Start.rand : Lnet/minecraft/util/SharedSeedRandom;
    ALOAD 6
    INVOKESTATIC net/minecraft/world/gen/feature/structure/VillagePieces.func_214838_a (Lnet/minecraft/world/gen/ChunkGenerator;Lnet/minecraft/world/gen/feature/template/TemplateManager;Lnet/minecraft/util/math/BlockPos;Ljava/util/List;Lnet/minecraft/util/SharedSeedRandom;Lnet/minecraft/world/gen/feature/structure/VillageConfig;)V
   L3
    LINENUMBER 64 L3
    ALOAD 0
    INVOKEVIRTUAL net/minecraft/world/gen/feature/structure/VillageStructure$Start.recalculateStructureSize ()V
   L4
    ALOAD 0
    INVOKESTATIC ar/com/messupetru/expansivevillages/VillageManager.fireVillageStructureStartEvent (Lnet/minecraft/world/gen/feature/structure/VillageStructure$Start;)V
   L5
    LINENUMBER 65 L4
    RETURN
   L6
    LOCALVARIABLE this Lnet/minecraft/world/gen/feature/structure/VillageStructure$Start; L0 L5 0
    LOCALVARIABLE generator Lnet/minecraft/world/gen/ChunkGenerator; L0 L5 1
    // signature Lnet/minecraft/world/gen/ChunkGenerator<*>;
    // declaration: generator extends net.minecraft.world.gen.ChunkGenerator<?>
    LOCALVARIABLE templateManagerIn Lnet/minecraft/world/gen/feature/template/TemplateManager; L0 L5 2
    LOCALVARIABLE chunkX I L0 L5 3
    LOCALVARIABLE chunkZ I L0 L5 4
    LOCALVARIABLE biomeIn Lnet/minecraft/world/biome/Biome; L0 L5 5
    LOCALVARIABLE villageconfig Lnet/minecraft/world/gen/feature/structure/VillageConfig; L1 L5 6
    LOCALVARIABLE blockpos Lnet/minecraft/util/math/BlockPos; L2 L5 7
    MAXSTACK = 6
    MAXLOCALS = 8
 */
function injectFireVillageStructureStartEvent(instructions) {
    var penultimateLabel;
    var arrayLength = instructions.size();
    var lastIsPassed = false;

    for (var i = arrayLength-1; i >= 0; --i) {
        var instruction = instructions.get(i);
        if (instruction.getType() == LABEL) {
            if(!lastIsPassed) {
                lastIsPassed = true;
            } else {
                penultimateLabel = instruction;
                print("Found injection point \"penultimate Label\" " + instruction);
                break;
            }
        }
    }
    if (!penultimateLabel)
        throw "Error: Couldn't find injection point \"penultimate Label\"!";

    var toInject = new InsnList();

    // Labels n stuff
    var originalInstructionsLabel = new LabelNode();

    toInject.add(new VarInsnNode(ALOAD, 0));

    // Make list of instructions to inject
    toInject.add(new MethodInsnNode(
        //int opcode
        INVOKESTATIC,
        //String owner
        "ar/com/messupetru/expansivevillages/VillageManager",
        //String name
        "fireVillageStructureStartEvent",
        //String descriptor
        "(Lnet/minecraft/world/gen/feature/structure/VillageStructure$Start;)V",
        //boolean isInterface
        false
    ));

    toInject.add(originalInstructionsLabel);

    // Inject instructions
    instructions.insert(penultimateLabel, toInject);
}














/**
 * Utility function to wrap all transformers in transformers that have logging
 *
 * @param {object} transformersObj All the transformers of this coremod.
 * @return {object} The transformersObj with all transformers wrapped.
 */
function wrapWithLogging(transformersObj) {

    var oldPrint = print;
    // Global variable because makeLoggingTransformerFunction is a separate function (thanks to scoping issues)
    currentPrintTransformer = null;
    print = function(msg) {
        if (currentPrintTransformer)
            msg = "[" + currentPrintTransformer + "]: " + msg;
        msg = "[ExpansiveVillages] " + msg;
        oldPrint(msg);
    };

    for (var transformerObjName in transformersObj) {
        var transformerObj = transformersObj[transformerObjName];

        var transformer = transformerObj["transformer"];
        if (!transformer)
            continue;

        transformerObj["transformer"] = makeLoggingTransformerFunction(transformerObjName, transformer);
    }
    return transformersObj;
}

/**
 * Utility function for making the wrapper transformer function with logging
 * Not part of {@link #wrapWithLogging) because of scoping issues (Nashhorn
 * doesn't support "let" which would fix the issues)
 *
 * @param {string} transformerObjName The name of the transformer
 * @param {transformer} transformer The transformer function
 * @return {function} A transformer that wraps the old transformer
 */
function makeLoggingTransformerFunction(transformerObjName, transformer) {
    return function(obj) {
        currentPrintTransformer = transformerObjName;
        print("Starting Transform.");
        obj = transformer(obj);
        currentPrintTransformer = null;
        return obj;
    }
}

/**
 * Utility function to wrap all method transformers in class transformers
 * to make them run after OptiFine's class transformers
 *
 * @param {object} transformersObj All the transformers of this coremod.
 * @return {object} The transformersObj with all method transformers wrapped.
 */
function wrapMethodTransformers(transformersObj) {

    for (var transformerObjName in transformersObj) {
        var transformerObj = transformersObj[transformerObjName];

        var target = transformerObj["target"];
        if (!target)
            continue;

        var type = target["type"];
        if (!type || !type.equals("METHOD"))
            continue;

        var clazz = target["class"];
        if (!clazz)
            continue;

        var methodName = target["methodName"];
        if (!methodName)
            continue;

        var mappedMethodName = ASMAPI.mapMethod(methodName);

        var methodDesc = target["methodDesc"];
        if (!methodDesc)
            continue;

        var methodTransformer = transformerObj["transformer"];
        if (!methodTransformer)
            continue;

        var newTransformerObjName = "(Method2ClassTransformerWrapper) " + transformerObjName;
        var newTransformerObj = {
            "target": {
                "type": "CLASS",
                "name": clazz,
            },
            "transformer": makeClass2MethodTransformerFunction(mappedMethodName, methodDesc, methodTransformer)
        };

        transformersObj[newTransformerObjName] = newTransformerObj;
        delete transformersObj[transformerObjName];
    }
    return transformersObj;
}

/**
 * Utility function for making the wrapper class transformer function
 * Not part of {@link #wrapMethodTransformers) because of scoping issues (Nashhorn
 * doesn't support "let" which would fix the issues)
 *
 * @param {string} mappedMethodName The (mapped) name of the target method
 * @param {string} methodDesc The description of the target method
 * @param {methodTransformer} transformer The method transformer function
 * @return {function} A class transformer that wraps the methodTransformer
 */
function makeClass2MethodTransformerFunction(mappedMethodName, methodDesc, methodTransformer) {
    return function(classNode) {
        var methods = classNode.methods;
        for (var i in methods) {
            var methodNode = methods[i];
            if (!methodNode.name.equals(mappedMethodName))
                continue;
            if (!methodNode.desc.equals(methodDesc))
                continue;
            methods[i] = methodTransformer(methodNode);
            break;
        }
        return classNode;
    };
}



















/**
 * Utility function for removing multiple instructions
 *
 * @param {InsnList} instructions The list of instructions to modify
 * @param {InsnList} startInstruction The first instruction of instructions to be removed
 * @param {InsnList} endInstruction The last instruction of instructions to be removed
 */
function removeBetweenInclusive(instructions, startInstruction, endInstruction) {
    var start = instructions.indexOf(startInstruction);
    var end = instructions.indexOf(endInstruction);
    for (var i = start; i <= end; ++i) {
        instructions.remove(instructions.get(start));
    }
}

/**
 * Util function to print a list of instructions for debugging
 *
 * @param {InsnList} instructions The list of instructions to print
 */
function printInstructions(instructions) {
    var arrayLength = instructions.size();
    var labelNames = {
        length: 0
    };
    for (var i = 0; i < arrayLength; ++i) {
        var text = getInstructionText(instructions.get(i), labelNames);
        if (text.length > 0) // Some instructions are ignored
            print(text);
    }
}

/**
 * Util function to get the text for an instruction
 *
 * @param {AbstractInsnNode} instruction The instruction to generate text for
 * @param {Map<int, string>} labelNames The names of the labels in the format Map<LabelHashCode, LabelName>
 */
function getInstructionText(instruction, labelNames) {
    var out = "";
    if (instruction.getType() != 8) // LABEL
        out += " "; // Nice formatting
    if (instruction.getOpcode() > 0) // Labels, Frames and LineNumbers don't have opcodes
        out += OPCODES[instruction.getOpcode()] + " ";
    switch (instruction.getType()) {
        default:
        case 0: // INSN
            break;
        case 1: // INT_INSN
            out += instruction.operand;
            break;
        case 2: // VAR_INSN
            out += instruction.var;
            break;
        case 3: // TYPE_INSN
            out += instruction.desc;
            break;
        case 4: // FIELD_INSN
            out += instruction.owner + "." + instruction.name + " " + instruction.desc;
            break;
        case 5: // METHOD_INSN
            out += instruction.owner + "." + instruction.name + " " + instruction.desc + " (" + instruction.itf + ")";
            break;
        case 6: // INVOKE_DYNAMIC_INSN
            out += instruction.name + " " + instruction.desc;
            break;
        case 7: // JUMP_INSN
            out += getLabelName(instruction.label, labelNames);
            break;
        case 8: // LABEL
            out += getLabelName(instruction.getLabel(), labelNames);
            break;
        case 9: // LDC_INSN
            out += instruction.cst;
            break;
        case 10: // IINC_INSN
            out += instruction.var + " " + instruction.incr;
            break;
        case 11: // TABLESWITCH_INSN
            out += instruction.min + " " + instruction.max;
            out += "\n";
            for (var i = 0; i < instruction.labels.length; ++i) {
                out += "   " + (instruction.min + i) + ": ";
                out += getLabelName(instruction.labels[i], labelNames);
                out += "\n";
            }
            out += "   " + "default: " + getLabelName(instruction.dflt, labelNames);
            break;
        case 12: // LOOKUPSWITCH_INSN
            for (var i = 0; i < instruction.labels.length; ++i) {
                out += "   " + instruction.keys[i] + ": ";
                out += getLabelName(instruction.labels[i], labelNames);
                out += "\n";
            }
            out += "   " + "default: " + getLabelName(instruction.dflt, labelNames);
            break;
        case 13: // MULTIANEWARRAY_INSN
            out += instruction.desc + " " + instruction.dims;
            break;
        case 14: // FRAME
            out += "FRAME";
            // Frames don't work because Nashhorn calls AbstractInsnNode#getType()
            // instead of accessing FrameNode#type for the code "instruction.type"
            // so there is no way to get the frame type of the FrameNode
            break;
        case 15: // LINENUMBER
            out += "LINENUMBER ";
            out += instruction.line + " " + getLabelName(instruction.start.getLabel(), labelNames);
            break;
    }
    return out;
}

/**
 * Util function to get the name for a LabelNode "instruction"
 *
 * @param {LabelNode} label The label to generate a name for
 * @param {Map<int, string>} labelNames The names of other labels in the format Map<LabelHashCode, LabelName>
 */
function getLabelName(label, labelNames) {
    var labelHashCode = label.hashCode();
    var labelName = labelNames[labelHashCode];
    if (labelName == undefined) {
        labelName = "L" + labelNames.length;
        labelNames[labelHashCode] = labelName;
        ++labelNames.length;
    }
    return labelName;
}

/** The names of the Java Virtual Machine opcodes. */
OPCODES = [
    "NOP", // 0 (0x0)
    "ACONST_NULL", // 1 (0x1)
    "ICONST_M1", // 2 (0x2)
    "ICONST_0", // 3 (0x3)
    "ICONST_1", // 4 (0x4)
    "ICONST_2", // 5 (0x5)
    "ICONST_3", // 6 (0x6)
    "ICONST_4", // 7 (0x7)
    "ICONST_5", // 8 (0x8)
    "LCONST_0", // 9 (0x9)
    "LCONST_1", // 10 (0xa)
    "FCONST_0", // 11 (0xb)
    "FCONST_1", // 12 (0xc)
    "FCONST_2", // 13 (0xd)
    "DCONST_0", // 14 (0xe)
    "DCONST_1", // 15 (0xf)
    "BIPUSH", // 16 (0x10)
    "SIPUSH", // 17 (0x11)
    "LDC", // 18 (0x12)
    "LDC_W", // 19 (0x13)
    "LDC2_W", // 20 (0x14)
    "ILOAD", // 21 (0x15)
    "LLOAD", // 22 (0x16)
    "FLOAD", // 23 (0x17)
    "DLOAD", // 24 (0x18)
    "ALOAD", // 25 (0x19)
    "ILOAD_0", // 26 (0x1a)
    "ILOAD_1", // 27 (0x1b)
    "ILOAD_2", // 28 (0x1c)
    "ILOAD_3", // 29 (0x1d)
    "LLOAD_0", // 30 (0x1e)
    "LLOAD_1", // 31 (0x1f)
    "LLOAD_2", // 32 (0x20)
    "LLOAD_3", // 33 (0x21)
    "FLOAD_0", // 34 (0x22)
    "FLOAD_1", // 35 (0x23)
    "FLOAD_2", // 36 (0x24)
    "FLOAD_3", // 37 (0x25)
    "DLOAD_0", // 38 (0x26)
    "DLOAD_1", // 39 (0x27)
    "DLOAD_2", // 40 (0x28)
    "DLOAD_3", // 41 (0x29)
    "ALOAD_0", // 42 (0x2a)
    "ALOAD_1", // 43 (0x2b)
    "ALOAD_2", // 44 (0x2c)
    "ALOAD_3", // 45 (0x2d)
    "IALOAD", // 46 (0x2e)
    "LALOAD", // 47 (0x2f)
    "FALOAD", // 48 (0x30)
    "DALOAD", // 49 (0x31)
    "AALOAD", // 50 (0x32)
    "BALOAD", // 51 (0x33)
    "CALOAD", // 52 (0x34)
    "SALOAD", // 53 (0x35)
    "ISTORE", // 54 (0x36)
    "LSTORE", // 55 (0x37)
    "FSTORE", // 56 (0x38)
    "DSTORE", // 57 (0x39)
    "ASTORE", // 58 (0x3a)
    "ISTORE_0", // 59 (0x3b)
    "ISTORE_1", // 60 (0x3c)
    "ISTORE_2", // 61 (0x3d)
    "ISTORE_3", // 62 (0x3e)
    "LSTORE_0", // 63 (0x3f)
    "LSTORE_1", // 64 (0x40)
    "LSTORE_2", // 65 (0x41)
    "LSTORE_3", // 66 (0x42)
    "FSTORE_0", // 67 (0x43)
    "FSTORE_1", // 68 (0x44)
    "FSTORE_2", // 69 (0x45)
    "FSTORE_3", // 70 (0x46)
    "DSTORE_0", // 71 (0x47)
    "DSTORE_1", // 72 (0x48)
    "DSTORE_2", // 73 (0x49)
    "DSTORE_3", // 74 (0x4a)
    "ASTORE_0", // 75 (0x4b)
    "ASTORE_1", // 76 (0x4c)
    "ASTORE_2", // 77 (0x4d)
    "ASTORE_3", // 78 (0x4e)
    "IASTORE", // 79 (0x4f)
    "LASTORE", // 80 (0x50)
    "FASTORE", // 81 (0x51)
    "DASTORE", // 82 (0x52)
    "AASTORE", // 83 (0x53)
    "BASTORE", // 84 (0x54)
    "CASTORE", // 85 (0x55)
    "SASTORE", // 86 (0x56)
    "POP", // 87 (0x57)
    "POP2", // 88 (0x58)
    "DUP", // 89 (0x59)
    "DUP_X1", // 90 (0x5a)
    "DUP_X2", // 91 (0x5b)
    "DUP2", // 92 (0x5c)
    "DUP2_X1", // 93 (0x5d)
    "DUP2_X2", // 94 (0x5e)
    "SWAP", // 95 (0x5f)
    "IADD", // 96 (0x60)
    "LADD", // 97 (0x61)
    "FADD", // 98 (0x62)
    "DADD", // 99 (0x63)
    "ISUB", // 100 (0x64)
    "LSUB", // 101 (0x65)
    "FSUB", // 102 (0x66)
    "DSUB", // 103 (0x67)
    "IMUL", // 104 (0x68)
    "LMUL", // 105 (0x69)
    "FMUL", // 106 (0x6a)
    "DMUL", // 107 (0x6b)
    "IDIV", // 108 (0x6c)
    "LDIV", // 109 (0x6d)
    "FDIV", // 110 (0x6e)
    "DDIV", // 111 (0x6f)
    "IREM", // 112 (0x70)
    "LREM", // 113 (0x71)
    "FREM", // 114 (0x72)
    "DREM", // 115 (0x73)
    "INEG", // 116 (0x74)
    "LNEG", // 117 (0x75)
    "FNEG", // 118 (0x76)
    "DNEG", // 119 (0x77)
    "ISHL", // 120 (0x78)
    "LSHL", // 121 (0x79)
    "ISHR", // 122 (0x7a)
    "LSHR", // 123 (0x7b)
    "IUSHR", // 124 (0x7c)
    "LUSHR", // 125 (0x7d)
    "IAND", // 126 (0x7e)
    "LAND", // 127 (0x7f)
    "IOR", // 128 (0x80)
    "LOR", // 129 (0x81)
    "IXOR", // 130 (0x82)
    "LXOR", // 131 (0x83)
    "IINC", // 132 (0x84)
    "I2L", // 133 (0x85)
    "I2F", // 134 (0x86)
    "I2D", // 135 (0x87)
    "L2I", // 136 (0x88)
    "L2F", // 137 (0x89)
    "L2D", // 138 (0x8a)
    "F2I", // 139 (0x8b)
    "F2L", // 140 (0x8c)
    "F2D", // 141 (0x8d)
    "D2I", // 142 (0x8e)
    "D2L", // 143 (0x8f)
    "D2F", // 144 (0x90)
    "I2B", // 145 (0x91)
    "I2C", // 146 (0x92)
    "I2S", // 147 (0x93)
    "LCMP", // 148 (0x94)
    "FCMPL", // 149 (0x95)
    "FCMPG", // 150 (0x96)
    "DCMPL", // 151 (0x97)
    "DCMPG", // 152 (0x98)
    "IFEQ", // 153 (0x99)
    "IFNE", // 154 (0x9a)
    "IFLT", // 155 (0x9b)
    "IFGE", // 156 (0x9c)
    "IFGT", // 157 (0x9d)
    "IFLE", // 158 (0x9e)
    "IF_ICMPEQ", // 159 (0x9f)
    "IF_ICMPNE", // 160 (0xa0)
    "IF_ICMPLT", // 161 (0xa1)
    "IF_ICMPGE", // 162 (0xa2)
    "IF_ICMPGT", // 163 (0xa3)
    "IF_ICMPLE", // 164 (0xa4)
    "IF_ACMPEQ", // 165 (0xa5)
    "IF_ACMPNE", // 166 (0xa6)
    "GOTO", // 167 (0xa7)
    "JSR", // 168 (0xa8)
    "RET", // 169 (0xa9)
    "TABLESWITCH", // 170 (0xaa)
    "LOOKUPSWITCH", // 171 (0xab)
    "IRETURN", // 172 (0xac)
    "LRETURN", // 173 (0xad)
    "FRETURN", // 174 (0xae)
    "DRETURN", // 175 (0xaf)
    "ARETURN", // 176 (0xb0)
    "RETURN", // 177 (0xb1)
    "GETSTATIC", // 178 (0xb2)
    "PUTSTATIC", // 179 (0xb3)
    "GETFIELD", // 180 (0xb4)
    "PUTFIELD", // 181 (0xb5)
    "INVOKEVIRTUAL", // 182 (0xb6)
    "INVOKESPECIAL", // 183 (0xb7)
    "INVOKESTATIC", // 184 (0xb8)
    "INVOKEINTERFACE", // 185 (0xb9)
    "INVOKEDYNAMIC", // 186 (0xba)
    "NEW", // 187 (0xbb)
    "NEWARRAY", // 188 (0xbc)
    "ANEWARRAY", // 189 (0xbd)
    "ARRAYLENGTH", // 190 (0xbe)
    "ATHROW", // 191 (0xbf)
    "CHECKCAST", // 192 (0xc0)
    "INSTANCEOF", // 193 (0xc1)
    "MONITORENTER", // 194 (0xc2)
    "MONITOREXIT", // 195 (0xc3)
    "WIDE", // 196 (0xc4)
    "MULTIANEWARRAY", // 197 (0xc5)
    "IFNULL", // 198 (0xc6)
    "IFNONNULL" // 199 (0xc7)
];
