package ru.shatskikh.entity.enums;

public enum FileType {

    PHOTO("photo"),
    VIDEO("video"),
    DOC("doc"),
    AUDIO("audio"),
    OTHER("other");

    private String type;

    FileType(String type) {this.type = type;}

    public static FileType determineType(String mimeType) {

        String type = mimeType.split("/")[0];

        return switch (type) {
            case "application" -> DOC;
            case "image" -> PHOTO;
            case "video" -> VIDEO;
            case "audio" -> AUDIO;
            default -> OTHER;
        };

    }

}
