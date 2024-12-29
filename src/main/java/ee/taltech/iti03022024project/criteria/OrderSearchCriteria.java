package ee.taltech.iti03022024project.criteria;

import jakarta.validation.constraints.PositiveOrZero;


public record OrderSearchCriteria(
        @PositiveOrZero
        Integer id,
        @PositiveOrZero
        Integer statusId,
        @PositiveOrZero
        Integer userId
) {


}
