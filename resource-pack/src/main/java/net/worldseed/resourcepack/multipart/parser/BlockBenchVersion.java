package net.worldseed.resourcepack.multipart.parser;

public enum BlockBenchVersion {
    V4(4),
    V5(5);

    private int major;

    BlockBenchVersion(int major) {
        this.major = major;
    }

    public boolean isHigherOrEqual(BlockBenchVersion version) {
        return this.major >= version.major;
    }

    public static BlockBenchVersion getFromString(String version) {
        String[] split = version.split("\\.");

        if (split.length == 0) {
            throw new IllegalArgumentException("Invalid version string: " + version);
        }

        for (BlockBenchVersion value : BlockBenchVersion.values()) {
            if(String.valueOf(value.major).equals(split[0])) {
                return value;
            }
        }
        return BlockBenchVersion.V5;
    }
}
