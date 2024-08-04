package com.example.sepatu9;

import org.opencv.core.CvType;
import org.opencv.ml.SVM;
import org.opencv.core.Mat;
import org.opencv.ml.Ml;
import java.io.File;

public class SVMClassifier {

    private SVM svm;

    public SVMClassifier() {
        loadModel();
    }

    private void loadModel() {
        try {
            // Path to the SVM model file (model must be trained and saved beforehand)
            String modelPath = "/sdcard/svm_model.yml";
            svm = SVM.load(modelPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String classify(float[] features) {
        if (svm == null) {
            return "Model not loaded";
        }

        Mat featureMat = new Mat(1, features.length, CvType.CV_32FC1);
        featureMat.put(0, 0, features);

        float response = svm.predict(featureMat);

        if (response == 1.0) {
            return "Cacat";
        } else {
            return "Tidak Cacat";
        }
    }
}