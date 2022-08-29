package ru.maksirep.chatbot.chatbot.helptools;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;

public class PhotoHelper {

    public void photoImprovement(String photoPath) {
        OpenCV.loadLocally();
        Mat loadedImage = loadImage(photoPath);
        Mat resizedImage = resizeImage(loadedImage);
        findTheText(resizedImage, photoPath);
    }


    private void findTheText(Mat resizedImage, String photoPath) {

        Mat adaptTrashMat = new Mat();
        Mat kernel = new Mat(new Size(3, 3), CV_8UC1, new Scalar(255));

        Imgproc.morphologyEx(resizedImage, adaptTrashMat, Imgproc.MORPH_OPEN, kernel);
        Imgproc.adaptiveThreshold(adaptTrashMat, adaptTrashMat, 125,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 11, 12);

        Mat grad = new Mat();

        Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));

        Imgproc.morphologyEx(adaptTrashMat, grad, Imgproc.MORPH_GRADIENT, morphKernel);

        Mat bw = new Mat();

        Imgproc.threshold(grad, bw, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        Mat connected = new Mat();

        morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 1));

        Imgproc.morphologyEx(bw, connected, Imgproc.MORPH_CLOSE, morphKernel);


        Mat mask = Mat.zeros(bw.size(), Imgcodecs.IMREAD_GRAYSCALE);

        List<MatOfPoint> contours = new ArrayList<>();

        Mat hierarchy = new Mat();

        Imgproc.findContours(connected, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        Mat whiteMat = new Mat(resizedImage.rows() + 202, resizedImage.cols() + 202, resizedImage.type());
        double height = whiteMat.height();
        double width = whiteMat.width();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                whiteMat.put(i, j, 255);
            }
        }

        int[] minMaxValues = new int[] {-1,-1,-1,-1};

        for(int idx = 0; idx < contours.size(); idx++) {
            Rect rect = Imgproc.boundingRect(contours.get(idx));

            Mat maskROI = new Mat(mask, rect);
            maskROI.setTo(new Scalar(0, 0, 0));

            Imgproc.drawContours(mask, contours, idx, new Scalar(255, 255, 255), Core.FILLED);

            double r = (double)Core.countNonZero(maskROI)/(rect.width*rect.height);
            if (r > .45 && (rect.height > 12 && rect.width > 12))
            {
                Mat rectMat = new Mat(adaptTrashMat, rect);
                matAddition(rectMat, whiteMat, rect.x, rect.y);
                findMinMaxValues(minMaxValues, rect.x, rect.x + rect.width, rect.y, rect.y + rect.height);
            }
        }
        Rect rectCrop = makeRectCrop(minMaxValues[0], minMaxValues[2],
                minMaxValues[1] - minMaxValues[0], minMaxValues[3] - minMaxValues[2],
                whiteMat.width(), whiteMat.height());
        Mat rectMat = new Mat(whiteMat,rectCrop);
        Imgproc.blur(rectMat, rectMat, new Size(2, 2));
        saveImage(rectMat, photoPath + "improved.jpg");
    }

    private Mat resizeImage(Mat loadedImage) {
        int imageSize = 1800;
        Mat mat = new Mat();
        Size size = loadedImage.size();
        double width = size.width;
        double height = size.height;
        double factor = 0;
        if (width > height)
            factor = imageSize/width;
        if (width <= height)
            factor = imageSize/height;
        factor = Math.max(1, factor);
        double[] doubleSize = new double[]{factor * width, factor * height};
        size.set(doubleSize);
        Imgproc.resize(loadedImage, mat, size, 1, 1, Imgproc.INTER_AREA);
        return mat;
    }


    private Mat matAddition(Mat rect, Mat mainMat, int x, int y) {
        int rectWidth = rect.width();
        int rectHeight = rect.height();
        for (int i = 0; i < rectHeight; i++) {
            for (int j = 0; j < rectWidth; j++) {
                int[] rectArray = new int[]{i, j};
                if (rect.get(rectArray)[0] <= 30.0)
                    mainMat.put(y + i, x + j, rect.get(rectArray));
            }
        }
        return mainMat;
    }

    private int[] findMinMaxValues(int[] minMaxArray, int minX, int maxX, int minY, int maxY) {
        if (minX <= minMaxArray[0] || minMaxArray[0] == -1) {
            minMaxArray[0] = minX;
        }
        if (maxX >= minMaxArray[1] || minMaxArray[1] == -1) {
            minMaxArray[1] = maxX;
        }
        if (minY <= minMaxArray[2] || minMaxArray[2] == -1) {
            minMaxArray[2] = minY;
        }
        if (maxY >= minMaxArray[3] || minMaxArray[3] == -1) {
            minMaxArray[3] = maxY;
        }
        return minMaxArray;
    }

    private Rect makeRectCrop(int x, int y, int width, int height, int matWidth, int matHeight) {
        Rect cropRect = new Rect();
        int addWidth = 0;
        int addHeight = 0;
        if (x - 100 >= 0) {
            cropRect.x = x - 100;
            addWidth += 100;
        } else if (x - 50 >= 0) {
            cropRect.x = x - 50;
            addWidth += 50;
        } else {
            cropRect.x = x;
        }

        if (y - 100 >= 0) {
            cropRect.y = y - 100;
            addHeight += 100;
        } else if (y - 50 >= 0) {
            cropRect.y = y - 50;
            addHeight += 50;
        } else {
            cropRect.y = y;
        }

        if (x + width + 100 <= matWidth) {
            cropRect.width = width + addWidth + 100;
        } else if ((x + width + 50 <= matWidth)) {
            cropRect.width = width + addWidth + 50;
        } else {
            cropRect.width = width;
        }

        if (y + height + 100 <= matHeight) {
            cropRect.height = height + addHeight + 100;
        } else if (y + height + 50 <= matHeight) {
            cropRect.height = height + addHeight + 50;
        } else {
            cropRect.height = height;
        }

        return cropRect;
    }

    private Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
    }

    private void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs.imwrite(targetPath, imageMatrix);
    }
}
