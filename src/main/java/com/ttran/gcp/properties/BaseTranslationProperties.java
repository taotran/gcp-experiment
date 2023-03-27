package com.ttran.gcp.properties;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BaseTranslationProperties {

    @JsonAlias({"in_path", "inPath"})
    private String inPath;
    @JsonAlias({"src_file_path", "srcFilePath"})
    private String srcFilePath;
    @JsonAlias({"out_path", "outPath"})
    private String outPath;
    @JsonAlias({"dest_file_path", "destFilePath"})
    private String destFilePath;
    @JsonAlias({"file_name", "file_name"})
    private String fileName;
}
