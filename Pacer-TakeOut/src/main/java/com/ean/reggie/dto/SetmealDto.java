package com.ean.reggie.dto;

import com.ean.reggie.entity.Setmeal;
import com.ean.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
