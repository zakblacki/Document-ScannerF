package com.nabeeltech.capturedoc.settings;

import android.graphics.Point;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ImageProcessorConfiguration implements Serializable, Cloneable {
    public RotateType rotateType;
    public ColorDepth outputColorDepth;
    public CropType cropType;
    public TargetFrameCropType targetFrameCropType;
    public DeskewType deskewType;
    public DocumentDimensions documentDimensions;
    public Integer outputDPI;
    //public final String ippString;
    public String advancedConfiguration;
    public BoundingTetragon croppingTetragon;

    public ImageProcessorConfiguration clone() {
        try {
            return (ImageProcessorConfiguration) super.clone();
        } catch (CloneNotSupportedException var2) {
            var2.printStackTrace();
            throw new InternalError("ImageProcessorConfiguration: unexpected clone not supported exception");
        }
    }

    public ImageProcessorConfiguration() {
        this.rotateType = RotateType.ROTATE_NONE;
        this.outputColorDepth = ColorDepth.COLOR;
        this.cropType = CropType.CROP_NONE;
        this.targetFrameCropType = TargetFrameCropType.TARGET_FRAME_CROP_OFF;
        this.deskewType = DeskewType.DESKEW_NONE;
        this.documentDimensions = null;
        this.outputDPI = 0;
        this.advancedConfiguration = "";
        this.croppingTetragon = null;
        //this.ippString = "";
    }

    public ImageProcessorConfiguration(String ipOperations)  {
        this.rotateType = RotateType.ROTATE_NONE;
        this.outputColorDepth = ColorDepth.COLOR;
        this.cropType = CropType.CROP_NONE;
        this.targetFrameCropType = TargetFrameCropType.TARGET_FRAME_CROP_OFF;
        this.deskewType = DeskewType.DESKEW_NONE;
        this.documentDimensions = null;
        this.outputDPI = 0;
        this.advancedConfiguration = "";
        this.croppingTetragon = null;
        //this.ippString = c.a(this, ipOperations);
    }

    public enum CropType {
        CROP_NONE,
        CROP_AUTO,
        CROP_TETRAGON;

        private CropType() {
        }
    }

    public enum TargetFrameCropType {
        TARGET_FRAME_CROP_OFF,
        TARGET_FRAME_CROP_ON;

        private TargetFrameCropType() {
        }
    }
    public enum DeskewType {
        DESKEW_NONE,
        DESKEW_BY_DOCUMENT_EDGES,
        DESKEW_BY_DOCUMENT_CONTENT;

        private DeskewType() {
        }
    }
    public enum ColorDepth {
        BITONAL(1),
        GRAYSCALE(8),
        COLOR(24);

        private ColorDepth(int value) {
        }
    }
    public enum RotateType {
        ROTATE_NONE(0),
        ROTATE_90(3),
        ROTATE_180(2),
        ROTATE_270(1),
        ROTATE_AUTO(4);

        public final int type;

        private RotateType(int type) {
            this.type = type;
        }
    }


    public class DocumentDimensions {
        private Float ij;
        private Float ik;

        public DocumentDimensions(Float shortEdge, Float longEdge) {
            this.ij = shortEdge;
            this.ik = longEdge;
        }

        public Float getShortEdge() {
            return this.ij;
        }

        public Float getLongEdge() {
            return this.ik;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                DocumentDimensions var2 = (DocumentDimensions)o;
                if (this.ij != null) {
                    if (this.ij.equals(var2.ij)) {
                        return this.ik != null ? this.ik.equals(var2.ik) : var2.ik == null;
                    }
                } else if (var2.ij == null) {
                    return this.ik != null ? this.ik.equals(var2.ik) : var2.ik == null;
                }

                return false;
            } else {
                return false;
            }
        }

        public int hashCode() {
            int var1 = this.ij != null ? this.ij.hashCode() : 0;
            var1 = 31 * var1 + (this.ik != null ? this.ik.hashCode() : 0);
            return var1;
        }
    }


    public static class BoundingTetragon implements Serializable, Cloneable {
        private static final long serialVersionUID = 6111310662346562354L;
        private static final String TAG = BoundingTetragon.class.getSimpleName();
        private transient Point cA = new Point(0, 0);
        private transient Point cB = new Point(0, 0);
        private transient Point cC = new Point(0, 0);
        private transient Point cD = new Point(0, 0);

        public BoundingTetragon() {
        }

        public BoundingTetragon clone() {
            BoundingTetragon var1;
            try {
                var1 = (BoundingTetragon)super.clone();
            } catch (CloneNotSupportedException var3) {
                var3.printStackTrace();
                throw new InternalError("BoundingTetragon: unexpected clone not supported exception");
            }

            if (var1.cA != null) {
                var1.cA = new Point(var1.cA);
            }

            if (var1.cB != null) {
                var1.cB = new Point(var1.cB);
            }

            if (var1.cC != null) {
                var1.cC = new Point(var1.cC);
            }

            if (var1.cD != null) {
                var1.cD = new Point(var1.cD);
            }

            return var1;
        }

        public BoundingTetragon(int topLeftX, int topLeftY, int topRightX, int topRightY, int bottomLeftX, int bottomLeftY, int bottomRightX, int bottomRightY) {
            this.cA.set(topLeftX, topLeftY);
            this.cB.set(topRightX, topRightY);
            this.cC.set(bottomLeftX, bottomLeftY);
            this.cD.set(bottomRightX, bottomRightY);
        }

        public BoundingTetragon(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
            this.cA.set(topLeft.x, topLeft.y);
            this.cB.set(topRight.x, topRight.y);
            this.cC.set(bottomLeft.x, bottomLeft.y);
            this.cD.set(bottomRight.x, bottomRight.y);
        }

        public Point getTopLeft() {
            return new Point(this.cA);
        }

        public void setTopLeft(Point topLeft) {
            if (topLeft == null) {
                this.cA = null;
            } else {
                this.cA.set(topLeft.x, topLeft.y);
            }

        }

        public Point getTopRight() {
            return new Point(this.cB);
        }

        public void setTopRight(Point topRight) {
            if (topRight == null) {
                this.cB = null;
            } else {
                this.cB.set(topRight.x, topRight.y);
            }

        }

        public Point getBottomLeft() {
            return new Point(this.cC);
        }

        public void setBottomLeft(Point bottomLeft) {
            if (bottomLeft == null) {
                this.cC = null;
            } else {
                this.cC.set(bottomLeft.x, bottomLeft.y);
            }

        }

        public Point getBottomRight() {
            return new Point(this.cD);
        }

        public void setBottomRight(Point bottomRight) {
            if (bottomRight == null) {
                this.cD = null;
            } else {
                this.cD.set(bottomRight.x, bottomRight.y);
            }

        }


        public static class KenVersion {
            public KenVersion() {
            }

            public static String getPackageVersion() {
                return "3.5.0.0.0.620";
            }
        }


        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(BoundingTetragon.class.getName());
            out.writeObject(KenVersion.getPackageVersion());
            out.writeObject(this.cA.x);
            out.writeObject(this.cA.y);
            out.writeObject(this.cB.x);
            out.writeObject(this.cB.y);
            out.writeObject(this.cC.x);
            out.writeObject(this.cC.y);
            out.writeObject(this.cD.x);
            out.writeObject(this.cD.y);
        }


            public boolean isValid() {
                boolean var1 = true;
                if (BoundingTetragon.this.cA.x > BoundingTetragon.this.cB.x) {
                    var1 = false;
                } else if (BoundingTetragon.this.cA.y > BoundingTetragon.this.cC.y) {
                    var1 = false;
                } else if (BoundingTetragon.this.cB.y > BoundingTetragon.this.cD.y) {
                    var1 = false;
                } else if (BoundingTetragon.this.cC.x > BoundingTetragon.this.cD.x) {
                    var1 = false;
                }

                return var1;
            }

            public boolean isAllZero() {
                boolean var1 = true;
                if (BoundingTetragon.this.cA.x != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cA.y != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cC.x != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cC.y != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cB.x != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cB.y != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cD.x != 0) {
                    var1 = false;
                } else if (BoundingTetragon.this.cD.y != 0) {
                    var1 = false;
                }

                return var1;
            }

            public String toExtCornersOpString(String loadop) {
                String var2 = loadop + "<PropertyName=\"CSkewDetect.use_external_page_corners.Bool\" Value=\"1\" Comment=\"DEFAULT   0\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_tl_x.double\" Value=\"" + BoundingTetragon.this.cA.x + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_tl_y.double\" Value=\"" + BoundingTetragon.this.cA.y + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_tr_x.double\" Value=\"" + BoundingTetragon.this.cB.x + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_tr_y.double\" Value=\"" + BoundingTetragon.this.cB.y + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_bl_x.double\" Value=\"" + BoundingTetragon.this.cC.x + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_bl_y.double\" Value=\"" + BoundingTetragon.this.cC.y + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_br_x.double\" Value=\"" + BoundingTetragon.this.cD.x + "\" />";
                var2 = var2 + loadop + "<PropertyName=\"CSkewDetect.external_page_corner_br_y.double\" Value=\"" + BoundingTetragon.this.cD.y + "\" />";
                return var2;
            }
        }

        public static enum Rotation {
            LEFT,
            RIGHT,
            FLIP;

            private Rotation() {
            }
        }
    }


