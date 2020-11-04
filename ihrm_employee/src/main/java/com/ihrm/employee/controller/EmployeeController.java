package com.ihrm.employee.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.poi.ExcelExportUtil;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.DownloadUtils;
import com.ihrm.domain.employee.*;
import com.ihrm.domain.employee.response.EmployeeReportResult;
import com.ihrm.employee.service.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseController {
    @Autowired
    private UserCompanyPersonalService userCompanyPersonalService;
    @Autowired
    private UserCompanyJobsService userCompanyJobsService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private TransferPositionService transferPositionService;
    @Autowired
    private PositiveService positiveService;
    @Autowired
    private ArchiveService archiveService;


    /**
     * 员工个人信息保存
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.PUT)
    public Result savePersonalInfo(@PathVariable(name = "id") String uid, @RequestBody Map map) throws Exception {
        UserCompanyPersonal sourceInfo = BeanMapUtils.mapToBean(map, UserCompanyPersonal.class);
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyPersonal();
        }
        sourceInfo.setUserId(uid);
        sourceInfo.setCompanyId(super.companyId);
        userCompanyPersonalService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工个人信息读取
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.GET)
    public Result findPersonalInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyPersonal info = userCompanyPersonalService.findById(uid);
        if (info == null) {
            info = new UserCompanyPersonal();
            info.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, info);
    }

    /**
     * 员工岗位信息保存
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.PUT)
    public Result saveJobsInfo(@PathVariable(name = "id") String uid, @RequestBody UserCompanyJobs sourceInfo) throws Exception {
        //更新员工岗位信息
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyJobs();
            sourceInfo.setUserId(uid);
            sourceInfo.setCompanyId(super.companyId);
        }
        userCompanyJobsService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工岗位信息读取
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.GET)
    public Result findJobsInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs info = userCompanyJobsService.findById(uid);
        if (info == null) {
            info = new UserCompanyJobs();
            info.setUserId(uid);
            info.setCompanyId(companyId);
        }
        return new Result(ResultCode.SUCCESS, info);
    }

    /**
     * 离职表单保存
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.PUT)
    public Result saveLeave(@PathVariable(name = "id") String uid, @RequestBody EmployeeResignation resignation) throws Exception {
        resignation.setUserId(uid);
        resignationService.save(resignation);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 离职表单读取
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.GET)
    public Result findLeave(@PathVariable(name = "id") String uid) throws Exception {
        EmployeeResignation resignation = resignationService.findById(uid);
        if (resignation == null) {
            resignation = new EmployeeResignation();
            resignation.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, resignation);
    }

    /**
     * 导入员工
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result importDatas(@RequestParam(name = "file") MultipartFile attachment) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单保存
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.PUT)
    public Result saveTransferPosition(@PathVariable(name = "id") String uid, @RequestBody EmployeeTransferPosition transferPosition) throws Exception {
        transferPosition.setUserId(uid);
        transferPositionService.save(transferPosition);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单读取
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.GET)
    public Result findTransferPosition(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs jobsInfo = userCompanyJobsService.findById(uid);
        if (jobsInfo == null) {
            jobsInfo = new UserCompanyJobs();
            jobsInfo.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, jobsInfo);
    }

    /**
     * 转正表单保存
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.PUT)
    public Result savePositive(@PathVariable(name = "id") String uid, @RequestBody EmployeePositive positive) throws Exception {
        positiveService.save(positive);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 转正表单读取
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.GET)
    public Result findPositive(@PathVariable(name = "id") String uid) throws Exception {
        EmployeePositive positive = positiveService.findById(uid);
        if (positive == null) {
            positive = new EmployeePositive();
            positive.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS, positive);
    }

    /**
     * 历史归档详情列表
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.GET)
    public Result archives(@PathVariable(name = "month") String month, @RequestParam(name = "type") Integer type) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 归档更新
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.PUT)
    public Result saveArchives(@PathVariable(name = "month") String month) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 历史归档列表
     */
    @RequestMapping(value = "/archives", method = RequestMethod.GET)
    public Result findArchives(@RequestParam(name = "pagesize") Integer pagesize, @RequestParam(name = "page") Integer page, @RequestParam(name = "year") String year) throws Exception {
        Map map = new HashMap();
        map.put("year", year);
        map.put("companyId", companyId);
        Page<EmployeeArchive> searchPage = archiveService.findSearch(map, page, pagesize);
        PageResult<EmployeeArchive> pr = new PageResult(searchPage.getTotalElements(), searchPage.getContent());
        return new Result(ResultCode.SUCCESS, pr);
    }

    /**
     * 当月人事报表导出
     * month: 2020-10 年月
     */
    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable(name = "month") String month) throws Exception {
        //TODO 先
        month = "2018-02";
        List<EmployeeReportResult> list = userCompanyPersonalService.findByReport(companyId, month);
//        Workbook wb=new XSSFWorkbook();
        SXSSFWorkbook wb = new SXSSFWorkbook(200);//阈值，内存中对象的最大数量
        Sheet sheet = wb.createSheet();
        String[] titles = "编号,姓名,手机,最高学历,国家地区,护照号,籍贯,生日,属相,入职时间,离职类型,离职原因,离职时间".split(",");
        Row row = sheet.createRow(0);
        int titleIndex = 0;
        for (String title : titles) {
            Cell cell = row.createCell(titleIndex++);
            cell.setCellValue(title);
        }
        int rowIndex = 1;
        Cell cell = null;
        for (int j = 1; j < 100000; j++) {
            for (EmployeeReportResult employeeReportResult : list) {
                row = sheet.createRow(rowIndex*j);
                // 编号,
                cell = row.createCell(0);
                cell.setCellValue(employeeReportResult.getUserId());
                // 姓名,
                cell = row.createCell(1);
                cell.setCellValue(employeeReportResult.getUsername());
                // 手机,
                cell = row.createCell(2);
                cell.setCellValue(employeeReportResult.getMobile());
                // 最高学历,
                cell = row.createCell(3);
                cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
                // 国家地区,
                cell = row.createCell(4);
                cell.setCellValue(employeeReportResult.getNationalArea());
                // 护照号,
                cell = row.createCell(5);
                cell.setCellValue(employeeReportResult.getPassportNo());
                // 籍贯,
                cell = row.createCell(6);
                cell.setCellValue(employeeReportResult.getNativePlace());
                // 生日,
                cell = row.createCell(7);
                cell.setCellValue(employeeReportResult.getBirthday());
                // 属相,
                cell = row.createCell(8);
                cell.setCellValue(employeeReportResult.getZodiac());
                // 入职时间,
                cell = row.createCell(9);
                cell.setCellValue(employeeReportResult.getTimeOfEntry());
                // 离职类型,
                cell = row.createCell(10);
                cell.setCellValue(employeeReportResult.getTypeOfTurnover());
                // 离职原因,
                cell = row.createCell(11);
                cell.setCellValue(employeeReportResult.getReasonsForLeaving());
                // 离职时间
                cell = row.createCell(12);
                cell.setCellValue(employeeReportResult.getResignationTime());
            }
        }
        //完成下载
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        wb.write(byteArrayOutputStream);
        new DownloadUtils().download(byteArrayOutputStream, response, month + "人事报表.xlsx");

    }


    /**
     * 以模版打印的形式-------当月人事报表导出
     * month: 2020-10 年月
     */
   /* @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable(name = "month") String month) throws Exception {
        //TODO 先
        month="2018-02";
       List<EmployeeReportResult> list=userCompanyPersonalService.findByReport(companyId,month);
        ClassPathResource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream fis =new FileInputStream(resource.getFile());

//        ExcelExportUtil<EmployeeReportResult> excelExportUtil = new ExcelExportUtil<>(EmployeeReportResult.class, 2, 2);
//        excelExportUtil.export(response,fis,list,month+"人事报表.xlsx");

        Workbook wb=new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        Row row = sheet.getRow(2);
        CellStyle[] styles=new CellStyle[row.getLastCellNum()];
        for(int i=0;i<row.getLastCellNum();i++){
            styles[i]=row.getCell(i).getCellStyle();
        }
        int rowIndex=2;
        Cell cell=null;
        for(int j=1;j<100000;j++){
            for (EmployeeReportResult employeeReportResult : list) {
                row=sheet.createRow(rowIndex*j);
                // 编号,
                cell = row.createCell(0);
                cell.setCellValue(employeeReportResult.getUserId());
                cell.setCellStyle(styles[0]);
                // 姓名,
                cell = row.createCell(1);
                cell.setCellValue(employeeReportResult.getUsername());
                cell.setCellStyle(styles[1]);
                // 手机,
                cell = row.createCell(2);
                cell.setCellValue(employeeReportResult.getMobile());
                cell.setCellStyle(styles[2]);
                // 最高学历,
                cell = row.createCell(3);
                cell.setCellValue(employeeReportResult.getTheHighestDegreeOfEducation());
                cell.setCellStyle(styles[3]);
                // 国家地区,
                cell = row.createCell(4);
                cell.setCellValue(employeeReportResult.getNationalArea());
                cell.setCellStyle(styles[4]);
                // 护照号,
                cell = row.createCell(5);
                cell.setCellValue(employeeReportResult.getPassportNo());
                cell.setCellStyle(styles[5]);
                // 籍贯,
                cell = row.createCell(6);
                cell.setCellValue(employeeReportResult.getNativePlace());
                cell.setCellStyle(styles[6]);
                // 生日,
                cell = row.createCell(7);
                cell.setCellValue(employeeReportResult.getBirthday());
                cell.setCellStyle(styles[7]);
                // 属相,
                cell = row.createCell(8);
                cell.setCellValue(employeeReportResult.getZodiac());
                cell.setCellStyle(styles[8]);
                // 入职时间,
                cell = row.createCell(9);
                cell.setCellValue(employeeReportResult.getTimeOfEntry());
                cell.setCellStyle(styles[9]);
                // 离职类型,
                cell = row.createCell(10);
                cell.setCellValue(employeeReportResult.getTypeOfTurnover());
                cell.setCellStyle(styles[10]);
                // 离职原因,
                cell = row.createCell(11);
                cell.setCellValue(employeeReportResult.getReasonsForLeaving());
                cell.setCellStyle(styles[11]);
                // 离职时间
                cell = row.createCell(12);
                cell.setCellValue(employeeReportResult.getResignationTime());
                cell.setCellStyle(styles[12]);
            }
        }
        //完成下载
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        wb.write(byteArrayOutputStream);
        new DownloadUtils().download(byteArrayOutputStream,response,month+"人事报表.xlsx");

    }*/

    /**
     * 打印员工详情报表
     */
    @RequestMapping(value = "/printProfile/{id}", method = RequestMethod.GET)
    public void printProfile(@PathVariable("id")String id) throws Exception {
        Resource resource=new ClassPathResource("pdf-template/profile.jasper");
        FileInputStream fis=new FileInputStream(resource.getFile());
        ServletOutputStream outputStream = response.getOutputStream();

        UserCompanyPersonal userCompanyPersonal = userCompanyPersonalService.findById(id);
        UserCompanyJobs userCompanyJobs = userCompanyJobsService.findById(id);
//        String staffPhoto="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1603252255068&di=36ec0830cf79f012eeaf75dab01bf18c&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202006%2F15%2F20200615144830_GQUKN.thumb.400_0.jpeg";
        Map<String,Object> map=new HashMap<>();
//        map.put("staffPhoto",staffPhoto);
        Map<String, Object> map1 = BeanMapUtils.beanToMap(userCompanyPersonal);
        Map<String, Object> map2 = BeanMapUtils.beanToMap(userCompanyJobs);
        map.putAll(map1);
        map.putAll(map2);

        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(fis,map , new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            outputStream.flush();
            outputStream.close();
        }


    }
}
