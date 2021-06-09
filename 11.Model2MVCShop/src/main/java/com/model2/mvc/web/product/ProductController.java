package com.model2.mvc.web.product;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.CookieGenerator;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.UploadFile;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;


//==> ��ǰ���� Controller
@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	///Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	//setter Method ���� ����
	
	@Autowired
    ServletContext servletContext;
	
		
	public ProductController(){
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
	
	/* file upload ��
	@RequestMapping(value="/addProduct", method=RequestMethod.POST)
	public String addProduct( @ModelAttribute("product") Product product, UploadFile uploadfile ,Model model ) throws Exception {

		System.out.println("/addProduct : POST");
		String tempManuDate = product.getManuDate().replaceAll("-", "");
		product.setManuDate(tempManuDate);
		
		//Business Logic
		productService.addProduct(product);
		System.out.println();
		model.addAttribute(product);
		
		return "forward:/product/addProduct.jsp";
	}
	*/
	
	///*
	@RequestMapping(value="/addProduct", method=RequestMethod.POST)
	public String addProduct( @RequestParam("thumbnail") MultipartFile file,
								@RequestParam("uploadFile") MultipartFile[] files,
								@ModelAttribute("product") Product product, Model model ) throws Exception {

		System.out.println("/addProduct : POST ����");
		
		String tempManuDate = product.getManuDate().replaceAll("-", "");
		product.setManuDate(tempManuDate);
		
		if(file!=null) {
			product.setFileName("thumbnail_"+file.getOriginalFilename());
		}
		int prodNo = productService.addProduct(product);
		System.out.println("addProduct() �Ϸ� prodNo : "+prodNo);
				
		//String realPath = servletContext.getRealPath("/resources/upload");
		String realPath = "C:\\Users\\aiacademy\\git\\09project\\09.Model2MVCShop(jQuery)\\src\\main\\webapp\\resources\\upload";
        String saveFolder = realPath + File.separator + prodNo;
        System.out.println("saveFolder : "+saveFolder);
        
        UploadFile uploadFile = null;
         
        File folder = new File(saveFolder);
        if(!folder.exists()) {
             folder.mkdirs();
        }
        file.transferTo(new File(folder, product.getFileName())) ;
        
        List<UploadFile> fileList = new ArrayList<UploadFile>();
        
        if(files!=null) {
	        for (MultipartFile tmpfile : files) {
	        	uploadFile = new UploadFile();
	        	uploadFile.setProdNo(prodNo);
	            String originalFileName = tmpfile.getOriginalFilename();
	            if (!originalFileName.isEmpty()) {
	                String saveFileName = UUID.randomUUID().toString() + originalFileName.substring(originalFileName.lastIndexOf('.'));
	                uploadFile.setOriginFileName(originalFileName);
	                uploadFile.setSaveFileName(saveFileName);
	                System.out.println(tmpfile.getOriginalFilename() + "   " + saveFileName);
	                tmpfile.transferTo(new File(folder, saveFileName));
	             }
	            System.out.println("uploadFile ���ÿϷ� : "+uploadFile);
	            fileList.add(uploadFile);
	        }
	        productService.addFile(fileList);
	        System.out.println("addFile() �Ϸ�");
        }
        
        model.addAttribute("fileList", fileList);
        model.addAttribute("product", product);
		
		return "forward:/product/addProduct.jsp";
	}
	//*/
	
	@RequestMapping(value="/getProduct")
	public String getProduct( @RequestParam("prodNo") int prodNo , Model model, HttpServletResponse response ) throws Exception {
		
		System.out.println("/getProduct");
		Map<String, Object> map = productService.getProduct(prodNo);
		
		model.addAttribute("product", ((Product)map.get("product")));
		model.addAttribute("uploadFiles", map.get("uploadFiles"));
		
		/*Spring ����ϸ鼭 ����
		Cookie cookie = new Cookie("history"+prodNo, URLEncoder.encode(product.getProdName()));
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
		System.out.println("history=prodNo ��Ű ����Ϸ� : "+cookie);	
		*/
		
		CookieGenerator cookie = new CookieGenerator();
	    cookie.setCookieName("history"+prodNo);
	    cookie.addCookie(response, URLEncoder.encode(((Product)map.get("product")).getProdName()));
	    cookie.setCookieMaxAge(-1);
	    System.out.println("history=prodNo ��Ű ����Ϸ� : "+cookie);
				
		return "forward:/product/getProduct.jsp";
	}
	
	@RequestMapping(value="/updateProduct/{prodNo}/{currentPage}", method=RequestMethod.GET)
	public String updateProduct( @PathVariable int prodNo , @PathVariable int currentPage, Model model ) throws Exception{

		System.out.println("/updateProduct : GET");
		//Business Logic
		Map<String, Object> map = productService.getProduct(prodNo);
		
		model.addAttribute("product", ((Product)map.get("product")));
		model.addAttribute("uploadFiles", map.get(map.get("uploadFiles")));
		model.addAttribute("currentPage", currentPage);
		
		return "forward:/product/updateProductView.jsp";
	}
	
	@RequestMapping(value="/updateProduct", method=RequestMethod.POST)
	public String updateProduct( @ModelAttribute("product") Product product , Model model ) throws Exception{

		System.out.println("/updateProduct : POST");
		//Business Logic
		productService.updateProduct(product);
		
		return "forward:/product/getProduct";
	}
	
	
	@RequestMapping("/listProduct")
	public String listProduct( @ModelAttribute("search") Search search , Model model , HttpServletRequest request) throws Exception{
		
		System.out.println("/listProduct");		
		
		System.out.println("search �˻��� : "+search.getSearchKeyword());
		System.out.println("search ���� : "+search.getSearchPriceMin()+" ~ "+search.getSearchPriceMax());
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic ����
		Map<String , Object> map=productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model �� View ����
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/product/listProduct.jsp";
	}
	
	@RequestMapping("/deleteProduct/{prodNo}")
	public String deleteProduct(@PathVariable int prodNo , Model model) throws Exception{
		
		System.out.println("/deleteProduct ���� / prodNo : "+prodNo);
		
		//DB���� ��ǰ���� ����
		productService.deleteProduct(prodNo);
		
		System.out.println("/deleteProduct �Ϸ�");
		
		return "redirect:/purchase/listSale?menu=manage";
	}
	
	
}