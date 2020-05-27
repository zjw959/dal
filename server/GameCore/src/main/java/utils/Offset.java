package utils;

import utils.lang.StrBuilderEx;

public class Offset {
    public int reader;
    public int writer;

    public String toString() {
        return StrBuilderEx.builder().a("[reader:").a(reader).a(",writer:").a(writer).a("]")
                .toString();
    }
}
