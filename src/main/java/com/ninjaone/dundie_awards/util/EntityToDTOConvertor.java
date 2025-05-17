package com.ninjaone.dundie_awards.util;

import com.ninjaone.dundie_awards.dto.ActivityDTO;
import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class EntityToDTOConvertor {

    private final ModelMapper modelMapper;

    public EntityToDTOConvertor(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        Converter<LocalDateTime, Date> localDateTimeToDateConverter = context -> {

            if(context.getSource() == null) {
                return null;
            }

            return  Date.from(context.getSource().atZone(ZoneId.systemDefault()).toInstant());
        };

        modelMapper.typeMap(Activity.class, ActivityDTO.class)
            .addMappings(mapper -> mapper.using(localDateTimeToDateConverter)
                .map(Activity::getOccurredAt, ActivityDTO::setOccurredAt));
    }

    public List<EmployeeDTO> getEmployeeDTOs(List<Employee> employees) {
        List<EmployeeDTO> dto = new LinkedList<>();
        employees.forEach(employee -> dto.add(modelMapper.map(employee, EmployeeDTO.class)));
        return dto;
    }

    public List<ActivityDTO> getActivityDTOs(List<Activity> activities) {
        List<ActivityDTO> dto = new LinkedList<>();
        activities.forEach(activity -> dto.add(modelMapper.map(activity, ActivityDTO.class)));
        return dto;
    }

    public <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }
}
