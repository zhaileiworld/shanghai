package com.tianqiauto.textile.weaving.controller.chengpinguanli;

import com.tianqiauto.textile.weaving.model.sys.Chengpin_ChuKu;
import com.tianqiauto.textile.weaving.model.sys.Chengpin_ChuKu_Shenqing;
import com.tianqiauto.textile.weaving.model.sys.Chengpin_Current;
import com.tianqiauto.textile.weaving.model.sys.Heyuehao;
import com.tianqiauto.textile.weaving.repository.ChengpinchukuRepository;
import com.tianqiauto.textile.weaving.repository.HeYueHaoRepository;
import com.tianqiauto.textile.weaving.service.chengpinguanli.ChengpinCurrentServer;
import com.tianqiauto.textile.weaving.service.chengpinguanli.ChengpinchukudengjiServer;
import com.tianqiauto.textile.weaving.service.chengpinguanli.ChengpinchukushenqingServer;
import com.tianqiauto.textile.weaving.util.log.Logger;
import com.tianqiauto.textile.weaving.util.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * @Author bjw
 * @Date 2019/3/30 11:04
 */
@RestController
@RequestMapping("chengpinguanli/chengpinchukudengji")
public class ChengpinchukudengjiController {

    @Autowired
    private ChengpinchukudengjiServer chengpinchukudengjiServer;

    @GetMapping("query_page")
    public Result findAll(Chengpin_ChuKu chengpinChuKu, @PageableDefault( sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        return Result.ok(chengpinchukudengjiServer.findAll(chengpinChuKu,pageable));
    }

    @Autowired
    private HeYueHaoRepository heYueHaoRepository;

    @Autowired
    private ChengpinCurrentServer chengpinCurrentServer;


    @PostMapping("add")
    @Logger(msg = "成品出库申请新增")
    @ApiOperation("成品管理-成品出库申请新增")
    @ResponseBody
    public Result add(@RequestBody Chengpin_ChuKu chengpinchuku){
        Heyuehao heyuehao = heYueHaoRepository.findById(chengpinchuku.getHeyuehao().getId()).get();
        Chengpin_Current chengpinCurrent = chengpinCurrentServer.findByHeyuehao(heyuehao);
        if(chengpinCurrent.getChangdu() < chengpinchuku.getChangdu()){
            return Result.result(666,"合约号："+chengpinCurrent.getHeyuehao().getName()+"成品库存不足！",chengpinchuku);
        }
        chengpinCurrent.setChangdu(chengpinCurrent.getChangdu()- chengpinchuku.getChangdu());
        chengpinCurrent.setHeyuehao(heyuehao);
        chengpinchuku = chengpinchukudengjiServer.save(chengpinchuku,chengpinCurrent);
        return Result.ok("添加成功！",chengpinchuku);
    }

    @GetMapping("delete")
    @Logger(msg = "成品管理-根据id删除成品出库登记信息")
    @ApiOperation("成品管理-根据id删除成品出库登记信息")
    public Result delete(Long id) {
        chengpinchukudengjiServer.deleteById(id);
        return Result.ok("删除成功！", id);
    }

    @PostMapping("update")
    @Logger(msg = "成品管理-修改成品出库登记信息")
    @ApiOperation("成品管理-修改成品出库登记信息")
    public Result update(@RequestBody Chengpin_ChuKu chengpinChuKu) {
        chengpinchukudengjiServer.update(chengpinChuKu);
        return Result.ok("修改成功！", chengpinChuKu);
    }

}
