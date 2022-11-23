package com.goodee.market.trade.item;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.goodee.market.member.MemberDTO;
import com.goodee.market.trade.item.like.ItemLikeDTO;
import com.goodee.market.trade.item.like.ItemLikeService;
import com.goodee.market.trade.review.ReviewDTO;
import com.goodee.market.util.MainPager;
import com.goodee.market.util.Pager;

@Controller
@RequestMapping("/trade/*")
//http://localhost/market/item/detail
public class ItemController{
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private ItemLikeService likeService;
	
	
//	@PostMapping("review")
//	@ResponseBody
//	public int setItemReview()throws Exception {
//		
//		
//		return "redirect:./trade/mypage";
//	}	
	
//	
	//--------------------- Comment --------------------------
	
		@PostMapping("commentUpdate")
		@ResponseBody
		public int setCommentUpdate(ItemCommentDTO itemCommentDTO)throws Exception{
			int result = itemService.setCommentUpdate(itemCommentDTO);
			return result;
		}
		
		@PostMapping("commentDelete")
		@ResponseBody
		public int setCommentDelete(ItemCommentDTO itemCommentDTO)throws Exception{
			int result = itemService.setCommentDelete(itemCommentDTO);
			return result;
		}
		
		
		@GetMapping("commentList")
		@ResponseBody
		public Map<String, Object> getcommentList(ItemCommentPager itemCommentPager)throws Exception{
			List<ItemCommentDTO> ar = itemService.getCommentList(itemCommentPager);
//			System.out.println("CommentList");
//			System.out.println(ar.size());			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", ar);
			map.put("pager", itemCommentPager);
			
			return map;
			
		}
		
	
	@PostMapping("commentAdd")
	@ResponseBody
	public String setCommentAdd(ItemCommentDTO itemCommentDTO)throws Exception {
		int result = itemService.setCommentAdd(itemCommentDTO);
		System.out.println(itemCommentDTO.getItemNum());
		System.out.println(itemCommentDTO.getWriter());
		System.out.println(itemCommentDTO.getContents());
		
		String jsonResult="{\"result\":\""+result+"\"}";
		return jsonResult;
	}
	
	@PostMapping("fileDelete")
	@ResponseBody
	public int setFileDelete(ItemImageDTO itemImageDTO, HttpSession session)throws Exception {
		int result = itemService.setFileDelete(itemImageDTO,session.getServletContext());
		return result;
	}
	
	
	//메인페이지
	@GetMapping(value = "main")
	public ModelAndView getTradeMain(MainPager mainPager, HttpSession session)throws Exception {
		System.out.println("중고 메인");
		
		ModelAndView mv = new ModelAndView();
		List<ItemDTO> ar = itemService.getTradeMain(mainPager);
		
		mv.addObject("list", ar);	
		mv.addObject("pager", mainPager);
		mv.setViewName("trade/main");
		return mv;
	}

	
	//카테고리 메인
	@GetMapping(value = "category")
	public ModelAndView getList(Pager pager)throws Exception {
		System.out.println("category");
		
		ModelAndView mv = new ModelAndView();
		List<ItemDTO> ar =itemService.getList(pager);
		
		mv.addObject("list", ar);
		mv.addObject("pager", pager);
		
		mv.setViewName("trade/category");
		return mv;
	}
	
	//찜 목록
	@GetMapping(value = "like")
	public ModelAndView getLikeList (HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
		mv.addObject("member", memberDTO);
		
		List<ItemDTO> ar = itemService.getLikeList(memberDTO);
		mv.addObject("list", ar);
		
		mv.setViewName("trade/like");
		return mv;
	}

	
	//글상세
	@GetMapping(value="detail")
	public ModelAndView getDetail(Long num, ModelAndView mv) throws Exception {
		//아이템 정보
		ItemDTO itemDTO =new ItemDTO();
		itemDTO.setItemNum(num);
		itemDTO = itemService.getDetail(itemDTO);
		mv.addObject("dto", itemDTO);
		
		//유저 개인 좋아요 정보
		ItemLikeDTO likeDTO = new ItemLikeDTO();
		
		//유저 정보
		MemberDTO memberDTO = new MemberDTO();
		likeDTO.setItemNum(itemDTO.getItemNum());
						// 판매자인지 로그인한 사람인지 확인해보기
		likeDTO.setMemberNum(itemDTO.getMemberNum());
		
		boolean isLikeExist = likeService.isLikeExist(likeDTO);
		mv.addObject("isLikeExist", isLikeExist);
		
		//아이템 좋아요 총갯수
		int itemLike = likeService.getItemLike(itemDTO);
		mv.addObject("like", itemLike);
		
		mv.setViewName("trade/detail");
		return mv;		
	} 
	

	//글작성
	@GetMapping(value="add")
	public String setAdd(ItemDTO itemDTO) throws Exception {
		return "trade/add";
	}
	
	
	@PostMapping(value="add")
	public ModelAndView setAdd(ItemDTO itemDTO,MultipartFile[] files,HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView(); 
		int item= itemService.setAdd(itemDTO,files,session.getServletContext());
		MemberDTO memberDTO= (MemberDTO)session.getAttribute("member");
		mv.addObject("itemDTO",itemDTO);
		mv.addObject("member", memberDTO);
		mv.setViewName("redirect:./category");
		System.out.println("add success");
		return mv;
	}
	
	
	//글수정
	@GetMapping(value="update")
	public ModelAndView setUpdate(Long num, ModelAndView mv)throws Exception {
		ItemDTO itemDTO = new ItemDTO();
		itemDTO.setItemNum(num);
		itemDTO = itemService.getDetail(itemDTO);
		mv.addObject("itemDTO", itemDTO);
		
		mv.setViewName("trade/update");
		return mv;
	
	}
	@PostMapping(value="update")
	public String setUpdate(ItemDTO itemDTO, MultipartFile[] files, HttpSession session)throws Exception {
		int result= itemService.setUpdate(itemDTO,files,session.getServletContext());
	return "redirect:./detail?num="+itemDTO.getItemNum();
	}
	
	
	
	//글삭제
	@GetMapping("delete")
	public String setDelete(Long num)throws Exception {
	ItemDTO itemDTO = new ItemDTO();
	itemDTO.setItemNum(num);
	int result= itemService.setDelete(itemDTO);
	return "redirect:./main";
	}
	

	
	
}
