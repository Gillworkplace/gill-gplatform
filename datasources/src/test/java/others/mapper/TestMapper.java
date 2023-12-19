package others.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import others.bean.Test;

/**
 * TestMapper
 *
 * @author gill
 * @version 2023/12/19
 **/
@Mapper
public interface TestMapper {

    String queryDbName();
}
