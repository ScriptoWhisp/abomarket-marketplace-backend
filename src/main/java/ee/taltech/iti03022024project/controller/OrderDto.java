package ee.taltech.iti03022024project.controller;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class OrderDto {
    private Integer id;
    private Integer userId;
    private Integer statusId;


}
