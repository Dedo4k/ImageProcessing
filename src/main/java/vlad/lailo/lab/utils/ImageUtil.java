package vlad.lailo.lab.utils;

import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import vlad.lailo.lab.enumerations.ColorGroups;

import java.util.*;
import java.util.stream.Collectors;

public class ImageUtil {

    public static List<Integer> getPixels(Image image) {
        List<Integer> pixels = new ArrayList<>();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                pixels.add((int) (image.getPixelReader().getColor(i, j).getRed() * 255));
            }
        }
        return pixels;
    }

    public static List<XYChart.Data<String, Long>> getHistogramData(Image image) {
        Map<Integer, Long> data = getPixels(image).stream()
                .collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));
        return data.entrySet().stream()
                .map(e -> new XYChart.Data<>(e.getKey().toString(), e.getValue())).collect(Collectors.toList());
    }

    public static Image getPreparationA(Image image, int threshold) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        writableImage.getPixelWriter().setPixels(0, 0,
                (int) writableImage.getWidth(),
                (int) writableImage.getHeight(),
                image.getPixelReader(),
                0, 0);
        for (int i = 0; i < writableImage.getWidth(); i++) {
            for (int j = 0; j < writableImage.getHeight(); j++) {
                if (writableImage.getPixelReader().getColor(i, j).getRed() * 255 < threshold) {
                    writableImage.getPixelWriter().setColor(i, j, Color.grayRgb(0));
                } else {
                    writableImage.getPixelWriter().setColor(i, j, Color.grayRgb(255));
                }
            }
        }
        return writableImage;
    }

    public static Image getPreparationB(Image image, int thresholdLow, int thresholdHigh) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        writableImage.getPixelWriter().setPixels(0, 0,
                (int) writableImage.getWidth(),
                (int) writableImage.getHeight(),
                image.getPixelReader(),
                0, 0);
        for (int i = 0; i < writableImage.getWidth(); i++) {
            for (int j = 0; j < writableImage.getHeight(); j++) {
                if (writableImage.getPixelReader().getColor(i, j).getRed() * 255 < thresholdLow ||
                        writableImage.getPixelReader().getColor(i, j).getRed() * 255 > thresholdHigh) {
                    writableImage.getPixelWriter().setColor(i, j, Color.grayRgb(0));
                } else {
                    writableImage.getPixelWriter().setColor(i, j, Color.grayRgb(255));
                }
            }
        }
        return writableImage;
    }

    public static Image applySobelFilter(Image image, int[][] c1, int[][] c2) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        writableImage.getPixelWriter().setPixels(0, 0,
                (int) writableImage.getWidth(),
                (int) writableImage.getHeight(),
                image.getPixelReader(),
                0, 0);

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int[][] core = getCore(image, i, j);

                int sum1 = 0;
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        sum1 += core[k][l] * c1[k][l];
                    }
                }

                int sum2 = 0;
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        sum2 += core[k][l] * c2[k][l];
                    }
                }
                int color = (int) Math.sqrt(Math.pow(sum1, 2) + Math.pow(sum2, 2));
                if (color > 255) {
                    color = 255;
                }
                writableImage.getPixelWriter().setColor(i, j, Color.grayRgb(color));
            }
        }
        return writableImage;
    }

    public static Image toGrayScale(Image image) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        writableImage.getPixelWriter().setPixels(0, 0,
                (int) writableImage.getWidth(),
                (int) writableImage.getHeight(),
                image.getPixelReader(),
                0, 0);
        for (int i = 0; i < writableImage.getWidth(); i++) {
            for (int j = 0; j < writableImage.getHeight(); j++) {
                writableImage.getPixelWriter().setColor(i, j, writableImage.getPixelReader().getColor(i, j).grayscale());
            }
        }
        return writableImage;
    }


    public static List<Group> getGroups(Image image, int minArea) {
        List<ImageUtil.Group> groups = new ArrayList<>();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getPixelReader().getColor(i, j).getRed() * 255 == 255) {
                    int finalI = i;
                    int finalJ = j;
                    if (groups.stream().noneMatch(g -> g.points.contains(new ImageUtil.Point(finalI, finalJ)))) {
                        ImageUtil.Group group = new ImageUtil.Group();
                        ImageUtil.getGroup(image, i, j, group);
                        if (group.points.size() >= minArea) {
                            groups.add(group);
                        }
                    }
                }
            }
        }
        return groups;
    }

    public static void getGroup(Image image, int x, int y, Group group) {
        if (x < 0 || x >= image.getWidth()) {
            return;
        }
        if (y < 0 || y >= image.getHeight()) {
            return;
        }
        if (x + 1 < image.getWidth() && image.getPixelReader().getColor(x + 1, y).getRed() * 255 == 255) {
            Point point = new Point(x + 1, y);
            if (!group.points.contains(point)) {
//                ((WritableImage) image).getPixelWriter().setColor(x + 1, y, Color.RED);
                group.points.add(point);
                getGroup(image, x + 1, y, group);
            }
        }
        if (y + 1 < image.getHeight() && image.getPixelReader().getColor(x, y + 1).getRed() * 255 == 255) {
            Point point = new Point(x, y + 1);
            if (!group.points.contains(point)) {
//                ((WritableImage) image).getPixelWriter().setColor(x, y + 1, Color.RED);
                group.points.add(new Point(x, y + 1));
                getGroup(image, x, y + 1, group);
            }
        }
        if (x - 1 >= 0 && image.getPixelReader().getColor(x - 1, y).getRed() * 255 == 255) {
            Point point = new Point(x - 1, y);
            if (!group.points.contains(point)) {
//                ((WritableImage) image).getPixelWriter().setColor(x - 1, y, Color.RED);
                group.points.add(point);
                getGroup(image, x - 1, y, group);
            }
        }
        if (y - 1 >= 0 && image.getPixelReader().getColor(x, y - 1).getRed() * 255 == 255) {
            Point point = new Point(x, y - 1);
            if (!group.points.contains(point)) {
//                ((WritableImage) image).getPixelWriter().setColor(x, y - 1, Color.RED);
                group.points.add(point);
                getGroup(image, x, y - 1, group);
            }
        }
    }

    public static void highlightGroups(Image image, Map<Integer, Set<Group>> groups) {
        groups.forEach((key, value) -> value.forEach(g -> {
            System.out.println(g);
            drawGroupWeightCenter(image, g);
            drawGroupPerimeter(image, g);
            drawGroupAxis(image, g);
            drawGroupBounds(image, g, key);
        }));
    }

    public static Map<Integer, Set<Group>> kAverage(List<Group> groups, int k) {
        Map<Integer, Set<Group.GroupStat>> grouped = new HashMap<>();
        List<Group.GroupStat> groupStats = groups.stream().map(Group.GroupStat::new).collect(Collectors.toList());
        List<Group.GroupStat> groupCenters = new ArrayList<>();
        boolean hasChanges = true;
        while (hasChanges) {
            hasChanges = false;
            if (groupCenters.isEmpty()) {
                groupCenters = getRandomGroups(groups, k);
            } else {
                groupCenters = getAverageGroups(grouped);
            }
            for (Group.GroupStat group : groupStats) {
                int i = relateGroup(group, groupCenters);
                grouped.putIfAbsent(i, new HashSet<>());
                if (!grouped.get(i).contains(group)) {
                    grouped.values().forEach(s -> s.remove(group));
                    grouped.get(i).add(group);
                    hasChanges = true;
                }
            }
        }
        return grouped.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(Group.GroupStat::getGroup).collect(Collectors.toSet())));
    }

    private static int relateGroup(Group.GroupStat group, List<Group.GroupStat> groupCenters) {
        double min = Double.MAX_VALUE;
        int i = 0;
        for (Group.GroupStat groupCenter : groupCenters) {
            double dist = countDistance(group, groupCenter);
            if (dist <= min) {
                min = dist;
                i = groupCenters.indexOf(groupCenter);
            }
        }
        return i;
    }

    private static double countDistance(Group.GroupStat group, Group.GroupStat groupCenter) {
        return Math.sqrt(Math.pow(group.getArea() - groupCenter.getArea(), 2) +
                Math.pow(group.getPerimeter() - groupCenter.getPerimeter(), 2) +
                Math.pow(group.getCompactness() - groupCenter.getCompactness(), 2) +
                Math.pow(group.getElongation() - groupCenter.getElongation(), 2));
    }

    private static List<Group.GroupStat> getAverageGroups(Map<Integer, Set<Group.GroupStat>> grouped) {
        return grouped.values().stream().map(ImageUtil::getAverageGroup).collect(Collectors.toList());
    }

    private static Group.GroupStat getAverageGroup(Set<Group.GroupStat> groups) {
        double area = groups.stream().mapToInt(Group.GroupStat::getArea).average().getAsDouble();
        double per = groups.stream().mapToInt(Group.GroupStat::getPerimeter).average().getAsDouble();
        double comp = groups.stream().mapToDouble(Group.GroupStat::getCompactness).average().getAsDouble();
        double elon = groups.stream().mapToDouble(Group.GroupStat::getElongation).average().getAsDouble();
        return new Group.GroupStat((int) area, (int) per, comp, elon);
    }

    private static List<Group.GroupStat> getRandomGroups(List<Group> groups, int n) {
        List<Group> randomized = new ArrayList<>();
        Set<Integer> generated = new HashSet<>();
        Random random = new Random();
        if (groups.size() < n) {
            throw new IllegalArgumentException("Number of groups is less then clusters number.");
        }
        for (int i = 0; i < n; i++) {
            int a;
            while (true) {
                a = random.nextInt(groups.size());
                if (!generated.contains(a)) {
                    generated.add(a);
                    break;
                }
            }
            randomized.add(groups.get(a));
        }
        return randomized.stream().map(Group.GroupStat::new).collect(Collectors.toList());
    }

    private static void drawGroupWeightCenter(Image image, Group group) {
        Point wc = group.getWeightCenter();
        ((WritableImage) image).getPixelWriter().setColor(wc.x, wc.y, Color.RED);
        ((WritableImage) image).getPixelWriter().setColor(wc.x + 1, wc.y, Color.RED);
        ((WritableImage) image).getPixelWriter().setColor(wc.x - 1, wc.y, Color.RED);
        ((WritableImage) image).getPixelWriter().setColor(wc.x, wc.y + 1, Color.RED);
        ((WritableImage) image).getPixelWriter().setColor(wc.x, wc.y - 1, Color.RED);
    }

    private static void drawGroupBounds(Image image, Group group, int index) {
        int maxX = group.points.stream().mapToInt(Point::getX).max().getAsInt();
        int minX = group.points.stream().mapToInt(Point::getX).min().getAsInt();
        int maxY = group.points.stream().mapToInt(Point::getY).max().getAsInt();
        int minY = group.points.stream().mapToInt(Point::getY).min().getAsInt();
        for (int i = minX; i <= maxX; i++) {
            ((WritableImage) image).getPixelWriter().setColor(i, minY, ColorGroups.get(index));
            ((WritableImage) image).getPixelWriter().setColor(i, maxY, ColorGroups.get(index));
        }
        for (int i = minY; i <= maxY; i++) {
            ((WritableImage) image).getPixelWriter().setColor(minX, i, ColorGroups.get(index));
            ((WritableImage) image).getPixelWriter().setColor(maxX, i, ColorGroups.get(index));
        }
    }

    private static void drawGroupPerimeter(Image image, Group group) {
        group.getPerimeterPoints()
                .forEach(p -> ((WritableImage) image).getPixelWriter().setColor(p.x, p.y, Color.GREEN));
    }

    private static void drawGroupAxis(Image image, Group group) {
        double f = group.getAxisOrientation();
        Point wc = group.getWeightCenter();
        long w = group.points.stream().mapToInt(Point::getX).distinct().count();
        for (int i = (int) (-w / 2); i < w / 2; i++) {
            try {
                ((WritableImage) image).getPixelWriter().setColor(wc.x + i, (int) (wc.y + i * f), Color.RED);
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    private static int[][] getCore(Image image, int x, int y) {
        int[][] core = new int[3][3];
        x = x == 0 ? x + 1 : x;
        x = x == image.getWidth() - 1 ? x - 1 : x;
        y = y == 0 ? y + 1 : y;
        y = y == image.getHeight() - 1 ? y - 1 : y;
        core[0][0] = (int) (image.getPixelReader().getColor(x - 1, y - 1).getRed() * 255);
        core[0][1] = (int) (image.getPixelReader().getColor(x - 1, y).getRed() * 255);
        core[0][2] = (int) (image.getPixelReader().getColor(x - 1, y + 1).getRed() * 255);
        core[1][0] = (int) (image.getPixelReader().getColor(x, y - 1).getRed() * 255);
        core[1][1] = (int) (image.getPixelReader().getColor(x, y).getRed() * 255);
        core[1][2] = (int) (image.getPixelReader().getColor(x, y + 1).getRed() * 255);
        core[2][0] = (int) (image.getPixelReader().getColor(x + 1, y - 1).getRed() * 255);
        core[2][1] = (int) (image.getPixelReader().getColor(x + 1, y).getRed() * 255);
        core[2][2] = (int) (image.getPixelReader().getColor(x + 1, y + 1).getRed() * 255);
        return core;
    }

    private static double centralMoment(Group group, int i, int j) {
        Point weightCenter = group.getWeightCenter();
        return group.points.stream()
                .mapToDouble(g -> Math.pow(g.x - weightCenter.x, i) * Math.pow(g.y - weightCenter.y, j)).sum();
    }

    public static class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "x = " + x + ", y = " + y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static class Group {
        public Set<Point> points = new HashSet<>();

        public int getArea() {
            return points.size();
        }

        public Point getWeightCenter() {
            int x = points.stream().mapToInt(Point::getX).sum() / getArea();
            int y = points.stream().mapToInt(Point::getY).sum() / getArea();
            return new Point(x, y);
        }

        public Set<Point> getPerimeterPoints() {
            Map<Integer, Map<String, Integer>> perX = new HashMap<>();
            Map<Integer, Map<String, Integer>> perY = new HashMap<>();
            for (Point p : points) {
                if (!perX.containsKey(p.x)) {
                    perX.put(p.x, new HashMap<>() {{
                        put("minY", p.y);
                        put("maxY", p.y);
                    }});
                } else {
                    perX.put(p.x, new HashMap<>() {{
                        put("minY", Math.min(p.y, perX.get(p.x).get("minY")));
                        put("maxY", Math.max(p.y, perX.get(p.x).get("maxY")));
                    }});
                }
                if (!perY.containsKey(p.y)) {
                    perY.put(p.y, new HashMap<>() {{
                        put("minX", p.x);
                        put("maxX", p.x);
                    }});
                } else {
                    perY.put(p.y, new HashMap<>() {{
                        put("minX", Math.min(p.x, perY.get(p.y).get("minX")));
                        put("maxX", Math.max(p.x, perY.get(p.y).get("maxX")));
                    }});
                }
            }
            return points.stream()
                    .filter(p -> (perX.get(p.x).get("minY") == p.y || perX.get(p.x).get("maxY") == p.y) ||
                            (perY.get(p.y).get("minX") == p.x || perY.get(p.y).get("maxX") == p.x))
                    .collect(Collectors.toSet());
        }

        public double getCompactness() {
            return Math.pow(getPerimeterPoints().size(), 2) / getArea();
        }

        public double getElongation() {
            return (centralMoment(this, 2, 0) + centralMoment(this, 0, 2) +
                    Math.sqrt(Math.pow(centralMoment(this, 2, 0) - centralMoment(this, 0, 2), 2) + 4 * Math.pow(centralMoment(this, 1, 1), 2)))
                    /
                    (centralMoment(this, 2, 0) + centralMoment(this, 0, 2) -
                            Math.sqrt(Math.pow(centralMoment(this, 2, 0) - centralMoment(this, 0, 2), 2) + 4 * Math.pow(centralMoment(this, 1, 1), 2)));
        }

        public double getAxisOrientation() {
            return Math.atan((2 * centralMoment(this, 1, 1)) / (centralMoment(this, 2, 0) - centralMoment(this, 0, 2))) / 2;
        }

        @Override
        public String toString() {
            return "Area: " + getArea() + "\n" +
                    "Weight Center: " + getWeightCenter().toString() + "\n" +
                    "Perimeter: " + getPerimeterPoints().size() + "\n" +
                    "Compactness: " + getCompactness() + "\n" +
                    "Elongation: " + getElongation() + "\n" +
                    "Axis Orientation: " + getAxisOrientation();
        }

        protected static class GroupStat {

            private Group group;
            private int area;
            private int perimeter;
            private double compactness;
            private double elongation;

            public GroupStat(Group group) {
                this.group = group;
                area = group.getArea();
                perimeter = group.getPerimeterPoints().size();
                compactness = group.getCompactness();
                elongation = group.getElongation();
            }

            public GroupStat(int area, int perimeter, double compactness, double elongation) {
                this.area = area;
                this.perimeter = perimeter;
                this.compactness = compactness;
                this.elongation = elongation;
            }

            public Group getGroup() {
                return group;
            }

            public void setGroup(Group group) {
                this.group = group;
            }

            public int getArea() {
                return area;
            }

            public void setArea(int area) {
                this.area = area;
            }

            public int getPerimeter() {
                return perimeter;
            }

            public void setPerimeter(int perimeter) {
                this.perimeter = perimeter;
            }

            public double getCompactness() {
                return compactness;
            }

            public void setCompactness(double compactness) {
                this.compactness = compactness;
            }

            public double getElongation() {
                return elongation;
            }

            public void setElongation(double elongation) {
                this.elongation = elongation;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                GroupStat groupStat = (GroupStat) o;
                return area == groupStat.area && perimeter == groupStat.perimeter && Double.compare(groupStat.compactness, compactness) == 0 && Double.compare(groupStat.elongation, elongation) == 0;
            }

            @Override
            public int hashCode() {
                return Objects.hash(area, perimeter, compactness, elongation);
            }
        }
    }
}
