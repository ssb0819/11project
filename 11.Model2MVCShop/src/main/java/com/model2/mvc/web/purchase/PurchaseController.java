package com.model2.mvc.web.purchase;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseService;


//==> ��ǰ���� Controller
@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	///Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	//setter Method ���� ����
		
	public PurchaseController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml ���� �Ұ�
	//==> �Ʒ��� �ΰ��� �ּ��� Ǯ�� �ǹ̸� Ȯ�� �Ұ�
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	
	@RequestMapping("/addPurchase")
	public String addPurchase( @ModelAttribute("purchase") Purchase purchase) throws Exception {

		System.out.println("/addPurchase ����");
		String tempManuDate = purchase.getDivyDate().replaceAll("-", "");
		purchase.setDivyDate(tempManuDate);
		
		//Business Logic
		purchaseService.addPurchase(purchase);
		//model.addAttribute(purchase);
		
		return "forward:/purchase/addPurchase.jsp";
	}
	
	@RequestMapping("/listSale")
	public String getSaleList( @ModelAttribute("search") Search search , Model model) throws Exception{
		
		System.out.println("/listSale ����");		
		
		System.out.println("search �˻��� : "+search.getSearchKeyword());
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic ����
		Map<String , Object> map=purchaseService.getSaleList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model �� View ����
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/purchase/listSale.jsp";
	}
	
	@RequestMapping("/getPurchase")
	public String getPurchase( ) throws Exception {
				
		return "";
	}
	
	@RequestMapping("/updatePurchaseView")
	public String updatePurchaseView( ) throws Exception{
	
		return "";
	}
	
	@RequestMapping("/updatePurchase")
	public String updatePurchase( ) throws Exception{
		
		return "";
	}
	
	
	@RequestMapping("/listPurchase")
	public String listPurchase() throws Exception{
		
		
		return "";
	}
	
	
	
}