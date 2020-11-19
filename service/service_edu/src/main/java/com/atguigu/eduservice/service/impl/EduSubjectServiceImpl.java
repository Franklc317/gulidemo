package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.entity.subject.TwoSubject;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-11-19
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            //文件输入流
            InputStream in = file.getInputStream();
            //调用方法进行读取
            EasyExcel.read(in, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //1 查询所有一级分类  parentid = 0
        QueryWrapper queryWrapperOne=new QueryWrapper();
        queryWrapperOne.eq("parent_id","0");
        List<EduSubject> oneSubject=baseMapper.selectList(queryWrapperOne);
        //查询二级分类
        QueryWrapper queryWrapperTwo=new QueryWrapper();
        queryWrapperOne.ne("parent_id","0");
        List<EduSubject> twoSubject=baseMapper.selectList(queryWrapperTwo);

        //封装成需要的结果list
        List<OneSubject> finalSubjectList = new ArrayList<>();
        for (int i = 0; i < oneSubject.size(); i++) {
            EduSubject eduSubject=oneSubject.get(i);
            OneSubject oneSubject1=new OneSubject();
            BeanUtils.copyProperties(eduSubject,oneSubject);
            finalSubjectList.add(oneSubject1);
            List<TwoSubject> twoSubjectList=new ArrayList<>();
            for (int i1 = 0; i1 < twoSubject.size(); i1++) {
                EduSubject eduSubject1=twoSubject.get(i1);
                if(eduSubject1.getParentId().equals(eduSubject.getId())) {
                    //把tSubject值复制到TwoSubject里面，放到twoFinalSubjectList里面
                    TwoSubject twoSubject2 = new TwoSubject();
                    BeanUtils.copyProperties(eduSubject1,twoSubject2);
                    twoSubjectList.add(twoSubject2);
                }
            }
            //把一级下面所有二级分类放到一级分类里面
            oneSubject1.setChildren(twoSubjectList);
        }
        return finalSubjectList;
    }
}
