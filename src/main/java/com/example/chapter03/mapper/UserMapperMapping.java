package com.example.chapter03.mapper;

import com.example.entity.User;
import com.example.entity.Vo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 映射器演示（XML 方式）—— 对应课程「resultType 与 resultMap」部分。
 *
 * <p>SQL 全部写在 {@code chapter03/mapper/UserMapperMapping.xml} 里，本接口只声明方法。</p>
 *
 * <p>两种映射机制（<b>互斥，不能同时用</b>）：</p>
 * <ol>
 *   <li><b>resultType</b>：MyBatis 默认映射，按「查询字段名 == 属性名」自动对应；
 *       字段名与属性名不一致时必须用 {@code as} 起别名，SQL 会显得啰嗦，适合单表；</li>
 *   <li><b>resultMap</b>：自定义 property-column 映射，SQL 不用起别名，
 *       可复用、可嵌套（association / collection），是关联查询的基础。</li>
 * </ol>
 */
public interface UserMapperMapping {

    /** resultType：字段名与属性名一致，自动映射。 */
    List<User> selectAll();

    /** resultType：单条查询。 */
    User selectById(@Param("id") Integer id);

    /** resultType + VO：SQL 里用 as 把字段别名改成 VO 的属性名。 */
    List<Vo> selectAllByVo();

    /** resultMap：同一个 VO，SQL 不用起别名，映射关系写在 resultMap 里。 */
    List<Vo> selectAllByVoResultMap();

    /** resultMap：实体类自己的自定义映射（显式写出每个字段，便于加 typeHandler 等）。 */
    List<User> selectAllByResultMap();

    /** XML 方式新增（useGeneratedKeys 回填自增主键）。 */
    int insertUser(User user);

    /** XML 方式修改。 */
    int updateUser(User user);

    /** XML 方式删除。 */
    int deleteById(@Param("id") Integer id);
}
