package automation;

public class InstalledSoftware {

    private int updateTypeId;
    private int version;

    public InstalledSoftware(int updateTypeId, int version) {
        this.updateTypeId = updateTypeId;
        this.version = version;
    }

    public int getUpdateTypeId() {
        return this.updateTypeId;
    }

    public int getVersion() {
        return this.version;
    }
}
