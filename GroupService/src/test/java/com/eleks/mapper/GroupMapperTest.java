package com.eleks.mapper;

import com.eleks.dto.GroupRequestDto;
import com.eleks.dto.GroupResponseDto;
import com.eleks.entity.GroupEntity;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class GroupMapperTest {

    private GroupMapper groupMapper = new GroupMapper();

    private GroupRequestDto groupRequestDtoExpected;

    private GroupResponseDto groupResponseDtoExpected;

    private GroupEntity groupEntityExpected;

    @Before
    public void init() {
        groupRequestDtoExpected = new GroupRequestDto("testGroupName", "USD", Arrays.asList(1L, 2L));

        groupResponseDtoExpected = new GroupResponseDto(1L, "testGroupName", "USD", Arrays.asList(1L, 2L));

        groupEntityExpected = GroupEntity.builder()
                .groupName("testGroupName")
                .currency("USD")
                .members(Arrays.asList(UserEntity.builder().userId(1L).build(), UserEntity.builder().userId(2L).build()))
                .build();
    }

    @Test
    public void convertToEntity_groupRequestDtoNotNull_shouldReturnProperEntity() {
        assertEquals(groupEntityExpected, groupMapper.convertToEntity(groupRequestDtoExpected));
    }

    @Test
    public void convertToEntity_groupRequestDtoIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> groupMapper.convertToEntity(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Group request dto can not be null");
    }

    @Test
    public void convertToEntityWithId_groupRequestDtoNotNull_shouldReturnProperEntity() {
        groupEntityExpected.setId(2L);

        assertEquals(groupEntityExpected, groupMapper.convertToEntity(2L, groupRequestDtoExpected));
    }

    @Test
    public void convertToEntityWithId_groupRequestDtoIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> groupMapper.convertToEntity(2L,null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Group request dto can not be null");
    }

    @Test
    public void convertToDto_groupEntityIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> groupMapper.convertToDto(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Group entity can not be null");
    }

    @Test
    public void convertToDto_groupEntityNotNull_shouldReturnProperGroupResponseDto() {
        groupEntityExpected.setId(1L);

        assertEquals(groupResponseDtoExpected, groupMapper.convertToDto(groupEntityExpected));
    }
}
