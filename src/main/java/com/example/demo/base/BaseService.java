package com.example.demo.base;

import com.example.demo.base.model.Base;
import com.example.demo.base.model.request.BaseReqDto;
import com.example.demo.base.model.response.BaseRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BaseService {
    private final BaseRepository baseRepository;

    public Long register(BaseReqDto dto){
        Base base = baseRepository.save(dto.toEntity());
        return base.getIdx();
    }

    public BaseRespDto instance(Long idx){
        Optional<Base> instance = baseRepository.findById(idx);
        return BaseRespDto.from(instance.orElse(null));
    }
}
