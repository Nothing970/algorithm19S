package ConvexHull;

import java.awt.*;
import java.io.PipedOutputStream;
import java.util.*;
import java.util.List;

import static java.lang.Math.*;

class Point{
    int x;
    int y;
    Point(){
        x = -1;
        y = -1;
    }

    Point(int _x, int _y){
        x = _x;
        y = _y;
    }

    @Override
    public String toString(){
        return "" + x + "," + y;
    }
}

public class ConvexHull {
    Point[] a, b, c;
    int total;
    int max = 100;
    Random random = new Random();
    double zero = 1.0e-8;

    /**
     * 生成对应数量的在矩阵中的点，且点不重复
     * @param num
     */
    public void generate(int num){
        total = num;
        a = new Point[total];
        b = new Point[total];
        c = new Point[total];

        boolean[][] used = new boolean[max+1][max+1];
        for(int i = 0; i < total; i++){
            int x = random.nextInt(max+1);
            int y = random.nextInt(max+1);
            if(used[x][y]){
                i--;
            }else{
                Point tmp = new Point(x, y);
                a[i] = tmp;
                b[i] = tmp;
                c[i] = tmp;
                used[x][y] = true;
            }
        }
        return;
    }

    /**
     * 显示点阵以及边界
     * @param boundary
     * @param points
     */
    public void show(List<Point> boundary, Point[] points){
        int[][] square = new int[max+1][max+1];
        for(Point p : points){
            square[p.x][p.y] = 1;
        }
        for(Point p : boundary){
            square[p.x][p.y] = 2;
        }
        for(int i = max; i >=0; i--){
            for(int j = 0; j < max+1; j++){
                if(square[i][j] == 1){
                    System.out.print("o ");
                }else if(square[i][j] == 2){
                    System.out.print("x ");
                }else{
                    System.out.print("  ");
                }
            }
            System.out.println("");
        }
    }

    /**
     * 测试用
     */
    public void show1(){
        Point limit = a[0];
        System.out.println(0 + " : (" + limit.x + "," + limit.y +") : " + 0);
        for(int i = 1; i < total; i++){
            Point p1 = a[i];
            int x1 = p1.x - limit.x;
            int y1 = p1.y - limit.y;
            double tan1;
            if(y1==0){
                tan1 = 90;
            }else{
                tan1 = toDegrees(atan(1.0d*x1/y1));
                tan1 = tan1 < 0 ? tan1 + 180.0 : tan1;
            }
            System.out.println(i + " : (" + p1.x + "," + p1.y +") : " + tan1);
        }
    }

    /**
     * 判断PA方向在AB方向的顺时针或逆时针处，可用于判断点p是否在三角形内
     * @param a
     * @param b
     * @param p
     * @return 1表示逆时针，-1表示顺时针, 0表示三点共线
     */
    public int g(Point a, Point b, Point p){
        int x1 = b.x - a.x;
        int y1 = b.y - a.y;
        int x2 = p.x - a.x;
        int y2 = p.y - a.y;
        double ret = x1 * y2 - y1 * x2;
        if(ret < zero){
            return -1;
        }else if(ret == 0){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 判断点p是否在abc所组成的三角形内
     * @param a
     * @param b
     * @param c
     * @param p
     * @return 1表示在，-1表示不在
     */
    public int inTriangle(Point a, Point b, Point c, Point p){
        if(g(a, b, c) == 0){
            return 0;
        }
        if(g(a, b, p) * g(a, b, c) >= 0 && g(b, c, p) * g(b, c, a) >= 0 && g(c, a, p) * g(c, a, b) >= 0){
            return 1;
        }
        return 0;
    }

    /**
     * 枚举法求凸包
     * @param points 点集
     * @return 边界点集
     */
    public  List<Point> BruteForce(Point[] points){
        List<Point> boundary = new ArrayList<Point>();
        boolean[] flag = new boolean[points.length];
        for(int i = 0; i < points.length - 3; i++){
            for(int j = i+1; j < points.length - 2; j++){
                for(int k = j+1; k < points.length - 1; k++){
                    for(int l = k+1; l < points.length; l++){
                        if(inTriangle(points[i], points[j], points[k], points[l]) == 1){
                            flag[l] = true;
                        }
                        if(inTriangle(points[j], points[k], points[l], points[i]) == 1){
                            flag[l] = true;
                        }
                        if(inTriangle(points[k], points[l], points[i], points[j]) == 1){
                            flag[l] = true;
                        }
                        if(inTriangle(points[l], points[i], points[j], points[k]) == 1){
                            flag[l] = true;
                        }
                    }
                }
            }
        }

        return boundary;
    }

    /**
     * 扫描法求凸包
     * @param points 点集
     * @return 边界点集
     */
    public List<Point> grahamScan(Point[] points){
        List<Point> boundary = new ArrayList<Point>();
        if(points.length <= 3){
            Collections.addAll(boundary, points);
            return boundary;
        }
        Point tmp = points[0];
        for(int i = 1; i < points.length; i++){
            if(tmp.x > points[i].x){
                tmp = points[i];
            }else if(tmp.x == points[i].x && tmp.y > points[i].y){
                tmp = points[i];
            }
        }

        final Point limit = tmp;
//        System.out.println(limit);
        Arrays.sort(points, new Comparator<Point>(){
            @Override
            public int compare(Point p1, Point p2){
                int x1 = p1.x - limit.x;
                int y1 = p1.y - limit.y;
                int x2 = p2.x - limit.x;
                int y2 = p2.y - limit.y;
                double tan1, tan2;
                if(x1 == 0 && y1 == 0){
                    return -1;
                }
                if(x2 == 0 && y2 == 0){
                    return 1;
                }
                if(y1 == 0 && y2 == 0){
                    return x2 - x1;
                }else if(y1 == 0){
                    tan1 = 90;
                    tan2 = toDegrees(atan(1.0d*x2/y2));
                    tan2 = tan2 < 0 ? tan2 + 180.0 : tan2;
                }else if(y2 == 0){
                    tan2 = 90;
                    tan1 = toDegrees(atan(1.0d*x1/y1));
                    tan1 = tan1 < 0 ? tan1 + 180.0 : tan1;
                }else{
                    tan1 = toDegrees(atan(1.0d*x1/y1));
                    tan1 = tan1 < 0 ? tan1 + 180.0 : tan1;
                    tan2 = toDegrees(atan(1.0d*x2/y2));
                    tan2 = tan2 < 0 ? tan2 + 180.0 : tan2;
                }
                if(tan1 < tan2){
                    return -1;
                }else if (tan1 > tan2){
                    return 1;
                }else{
                    return p1.y - p2.y;
                }
            }
        });
        Stack<Point> stack = new Stack<Point>();
        stack.push(points[0]);
        stack.push(points[1]);
        stack.push(points[2]);
        for(int i = 3; i < points.length; i++){
            while(stack.size() > 2){
                Point top = stack.pop();
                Point topNext = stack.peek();
                Point cur = points[i];
                int x1 = top.x - topNext.x;
                int y1 = top.y - topNext.y;
                int x2 = cur.x - topNext.x;
                int y2 = cur.y - topNext.y;
                if(x1*y2 - y1*x2 < zero){
                    stack.push(top);
                    break;
                }
            }
            stack.push(points[i]);
        }
        boundary = new ArrayList<Point>(stack);
        return boundary;
    }

    /**
     * 分治法求凸包
     * @param points 点集
     * @param start 开始坐标
     * @param end 结束坐标
     * @return 边界点集
     */
    public List<Point> divideConquer(Point[] points, int start, int end){
        List<Point> boundary = new ArrayList<Point>();
        if(end - start + 1 <= 3){
            for(int i = start; i <= end; i++){
                boundary.add(points[i]);
            }
            return boundary;
        }
        int mid = (end + start) / 2;
        List<Point> boundary1 = divideConquer(points, start, mid);
        List<Point> boundary2 = divideConquer(points, mid+1, end);
        boundary.addAll(boundary1);
        boundary.addAll(boundary2);

        Point[] tmpBoundary = new Point[boundary.size()];
        boundary.toArray(tmpBoundary);
        boundary = grahamScan(tmpBoundary);

        if(start == 0 && end == points.length - 1){
            int right = -1;
            for(int i = 0; i + 1 < boundary.size(); i++){
                if(boundary.get(i).y == boundary.get(i+1).y){
                    right = Math.max(right, boundary.get(i).y);
                }
            }
            for(Point p : points){
                if(p.y == right){
                    boundary.add(p);
                }
            }
        }

        return boundary;
    }

    public static void main(String[] args) {
        ConvexHull convexHull = new ConvexHull();
        convexHull.generate(10000);
        convexHull.show(new ArrayList<Point>(), convexHull.a);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        List<Point> boundary1 = convexHull.grahamScan(convexHull.a);
        convexHull.show(boundary1, convexHull.a);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

//        convexHull.b = new Point[convexHull.max*convexHull.max];
//        int k = 0;
//        for(int i = 0; i < convexHull.max; i++){
//            for(int j = 0; j < convexHull.max; j++){
//                convexHull.b[k++] = new Point(i,j);
//            }
//        }

        Arrays.sort(convexHull.b, new Comparator<Point>(){
            public int compare(Point p1, Point p2) {
                if(p1.y < p2.y)
                {
                    return 1;
                }else if(p1.y > p2.y){
                    return -1;
                }else{
                    if(p1.x < p2.x){
                        return -1;
                    }else if(p1.x > p2.x){
                        return 1;
                    }
                }
                return 0;
            }
        });
        List<Point> boundary2 = convexHull.divideConquer(convexHull.b, 0, convexHull.b.length - 1);
        convexHull.show(boundary2, convexHull.b);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

}
