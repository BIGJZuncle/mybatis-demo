package com.example.entity;

/**
 * VO（View Object）：用于封装「查询结果与实体属性名不一致」的场景。
 *
 * <p>对应映射器演示里的两种写法：</p>
 * <ul>
 *   <li><b>resultType + 别名</b>：
 *       {@code SELECT id AS p1, username AS p2, password AS p3 FROM user}
 *       —— MyBatis 按<b>查询字段名</b>映射到属性，所以字段名必须用 as 改成属性名；</li>
 *   <li><b>resultMap</b>：{@code SELECT id, username, password FROM user}
 *       配合 {@code <resultMap>} 里的 property/column 映射，SQL 不用起别名。</li>
 * </ul>
 *
 * <p>注：原课程文件里字段名写成 {@code P1/P2/P3}，这里按 Java 命名规范改成小写
 * {@code p1/p2/p3}，getter 仍是 {@code getP1()}，MyBatis 映射结果不变。</p>
 */
public class Vo {

    private Integer p1;
    private String p2;
    private String p3;

    public Vo() {
    }

    public Vo(Integer p1, String p2, String p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Integer getP1() {
        return p1;
    }

    public void setP1(Integer p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public String getP3() {
        return p3;
    }

    public void setP3(String p3) {
        this.p3 = p3;
    }

    @Override
    public String toString() {
        return "Vo{" +
                "p1=" + p1 +
                ", p2='" + p2 + '\'' +
                ", p3='" + p3 + '\'' +
                '}';
    }
}
