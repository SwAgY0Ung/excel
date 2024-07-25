package com.yhy.excel;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * @author Echo
 * @date 2024/7/22 17:03
 */
@SpringBootTest
public class test {
    @Test
    public void importBook() {
        File file = new File("D:\\yhy\\app\\google\\《红楼梦》_qinkan.net.epub");
        InputStream in = null;
        try {
            //从输入流当中读取epub格式文件
            EpubReader reader = new EpubReader();
            in = new FileInputStream(file);
            Book book = reader.readEpub(in);
            //获取到书本的头部信息 比如，作者，出版社，语言等；
            Metadata metadata = book.getMetadata();
            System.out.println("FirstTitle为："+metadata.getFirstTitle());
            //获取到书本的全部资源 一个Resource就是书籍的一部分资源，这资源信息可以是html,css,js,图片等；
            Resources resources = book.getResources();
            System.out.println("所有资源数量为："+resources.size());
            //获取所有的资源数据
            Collection<String> allHrefs = resources.getAllHrefs();
            for (String href : allHrefs) {
                Resource resource = resources.getByHref(href);
                //data就是资源的内容数据，可能是css,html,图片等等
                byte[] data = resource.getData();
                // 获取到内容的类型  css,html,还是图片
                MediaType mediaType = resource.getMediaType();
            }
            //获取到书本的内容资源
            List<Resource> contents = book.getContents();
            for (Resource content : contents) {
                byte[] data = content.getData();
                String utf_8 = new String(data, StandardCharsets.UTF_8);
                System.out.println(utf_8);
            }
            System.out.println("内容资源数量为："+contents.size());
            //获取到书本的spine资源 线性排序   书籍的阅读顺序，是一个线性的顺序。通过Spine可以知道应该按照怎样的章节（注：这里所说的章节其实就是resource，不仅是书籍文本内容哦~下同）顺序去阅读，并且通过Spine可以找到对应章节的内容。
            Spine spine = book.getSpine();
            System.out.println("spine资源数量为："+spine.size());
            //通过spine获取所有的数据
            List<SpineReference> spineReferences = spine.getSpineReferences();
            for (SpineReference spineReference : spineReferences) {
                Resource resource = spineReference.getResource();
                //data就是资源的内容数据，可能是css,html,图片等等
                byte[] data = resource.getData();
                // 获取到内容的类型  css,html,还是图片
                MediaType mediaType = resource.getMediaType();
            }
            //获取到书本的目录资源
            TableOfContents tableOfContents = book.getTableOfContents();
            System.out.println("目录资源数量为："+tableOfContents.size());
            //获取到目录对应的资源数据
            List<TOCReference> tocReferences = tableOfContents.getTocReferences();
            for (TOCReference tocReference : tocReferences) {
                Resource resource = tocReference.getResource();
                //data就是资源的内容数据，可能是css,html,图片等等
                byte[] data = resource.getData();
                // 获取到内容的类型  css,html,还是图片
                MediaType mediaType = resource.getMediaType();
                if(tocReference.getChildren().size()>0){
                    //获取子目录的内容
                }
            }



            //TODO 获取某一章节的内容
            //通过index获取
            int index = 0;
            Resource byIndex = book.getSpine().getResource(index);

//通过href获取
            String href = "/images/1.png";
            Resource byHref = book.getResources().getByHref(href);

//通过id
            String id = "chapter01";
            Resource byId = book.getResources().getById(id);

//特殊的resource,可以直接获取
            Resource coverImage = book.getCoverImage();
            Resource coverPage = book.getCoverPage();
            Resource ncxResource = book.getNcxResource();
            Resource opfResource = book.getOpfResource();

//其他
            Resource resource1 = book.getSpine().getSpineReferences().get(0).getResource();
            Resource resource = book.getGuide().getReferences().get(0).getResource();
            System.out.println("解析完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //一定要关闭资源
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
