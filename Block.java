import java.util.HashMap;

public class Block {
    private HashMap<String,Variable> variableHashMap;
    private int blockNum;
    private int fatherBlock;

    public Block(int blockNum,int fatherBlock){
        this.blockNum = blockNum;
        this.fatherBlock = fatherBlock;
        this.variableHashMap = new HashMap<>();
    }

    public HashMap<String, Variable> getVariableHashMap() {
        return variableHashMap;
    }

    public void setVariableHashMap(HashMap<String, Variable> variableHashMap) {
        this.variableHashMap = variableHashMap;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    public int getFatherBlock() {
        return fatherBlock;
    }

    public void setFatherBlock(int fatherBlock) {
        this.fatherBlock = fatherBlock;
    }
}
