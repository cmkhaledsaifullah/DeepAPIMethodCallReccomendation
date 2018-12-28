package org.srlab.damca.config;

import java.io.File;

public class Config {
    public static final Boolean IS_DAMCA_COLLECT = Boolean.FALSE;
    public static final Boolean IS_SLP_COLLECT = Boolean.FALSE;
    public static final Boolean IS_SLAMC_COLLECT = Boolean.TRUE;

    private static final String ROOT_FOLDER = "/home/khaledkucse/Project/java/IntellIJSourceCodeAnalysis/DAMCA_Context_Collector/";

    public static final String ROOT_PATH = ROOT_FOLDER + "subject_systems";

    public static final String LOG_FILE_PATH = ROOT_FOLDER + "log.txt";

    public static final String INDIVIDUAL_FILE_PATH = ROOT_FOLDER + "models/";
    public static final String REPOSITORY_NAME = "jhotdraw";
    public static final String REPOSITORY_PATH = ROOT_PATH + File.separator + REPOSITORY_NAME;
    public static final String TEST_REPOSITORY_PATH = ROOT_FOLDER + "/test_reprository";
    public static final String MODEL_ENTRY_OUTPUT_PATH = ROOT_FOLDER + "dataset/";


    //For comparison with SLP_CORE
    public static final String SLP_DATA_OUTPUT_PATH = ROOT_FOLDER + "slp_dataset/";
    public static final String SLAMC_DATA_OUTPUT_PATH = ROOT_FOLDER + "slamc_dataset/";


    public static final String REPOSITORY_REVISION_PATH = ROOT_PATH + File.separator + REPOSITORY_NAME + "_revisions";
    public static final String EXTERNAL_DEPENDENCY_PATH = ROOT_PATH + File.separator + REPOSITORY_NAME + "_dependencies";

    public static final String[] FILE_EXTENSIONS = {".java"};
    public static final String FRAMEWORKS[] = {"javax.swing.","java.awt.","java.util.","java.io.","java.math.","java.net.","java.nio.","java.lang."};
    //public static final String FRAMEWORKS[] = {"javax.swing."};

    public static boolean isInteresting(String qualifiedTypeName) {
        for (String prefix : FRAMEWORKS) {
            if (qualifiedTypeName.startsWith(prefix)) return true;
        }
        return false;
    }

    public static String getRepositoryFolderName() {
        File file = new File(Config.REPOSITORY_PATH);
        return file.getName();
    }
}
