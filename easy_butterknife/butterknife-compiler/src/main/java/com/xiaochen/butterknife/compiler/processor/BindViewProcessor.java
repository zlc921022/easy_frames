package com.xiaochen.butterknife.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.xiaochen.butterknife.annotation.BindView;
import com.xiaochen.butterknife.compiler.utils.Constant;
import com.xiaochen.butterknife.compiler.utils.Log;
import com.xiaochen.butterknife.compiler.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


@AutoService(Processor.class)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 */
@SupportedAnnotationTypes(Constant.ANNOTATION_TYPE_BIND_VIEW)
/**
 * @author : xiaochen
 * Create Date: 2020/05/16
 * Email: zlc921022@163.com
 * @author admin
 */
public class BindViewProcessor extends AbstractProcessor {

    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;

    private Log log;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //获得apt的日志输出
        log = Log.newLog(processingEnvironment.getMessager());
        filerUtils = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(BindView.class.getCanonicalName());
    }

    /**
     * @param set              使用了支持处理注解的节点集合
     * @param roundEnvironment 表示当前或是之前的运行环境,可以通过该对象查找找到的注解。
     * @return true 表示后续处理器不会再处理(已经处理)
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("BindViewProcessor init");
        if( !Utils.isEmpty(set)){
            processBindViews(roundEnvironment);
            return true;
        }
        return false;
    }

    private void processBindViews(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getRootElements()) {
            String packageName = element.getEnclosingElement().toString();
            String classStr = element.getSimpleName().toString();
            log.i("packageStr: "+ packageName);
            log.i("classStr: "+ classStr);
            // 1. 构造要生成的类
            ClassName className = ClassName.get(packageName,
                    classStr + Constant.SUFFIX_NAME);
            // 2. 创建构造方法 参数
            MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(packageName,classStr),"activity");
            //3. 查找BindView 注解对应的字段 方法内容实现
            boolean hasBindView = false;
            for (Element e: element.getEnclosedElements()) {
                BindView bindView = e.getAnnotation(BindView.class);
                if(bindView != null){
                    hasBindView = true;
                    builder.addStatement("activity.$N = activity.findViewById($L)",
                            e.getSimpleName(),bindView.value());
                }
            }

            if(hasBindView){
                // 4. 构建类
                TypeSpec typeSpec = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(builder.build())
                        .build();
                // 5. 写入文件
                try {
                    JavaFile.builder(packageName,typeSpec)
                            .build()
                            .writeTo(filerUtils);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
