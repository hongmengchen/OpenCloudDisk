package cn.allene.vote.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.allene.vote.dao.VoteDaoImpl;
import cn.allene.vote.vo.SubItemCustom;
import cn.allene.vote.vo.Subject;
import net.sf.json.JSONArray;

@SuppressWarnings("serial")
@WebServlet("/voteServlet")
public class VoteServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		String method = request.getParameter("method");
		VoteDaoImpl vDaoImpl = new VoteDaoImpl();
		//获取所有的投票项目
		if("subjects".equals(method)){
			List<Subject> subjects = vDaoImpl.getAllSubject();
			JSONArray fromObject = JSONArray.fromObject(subjects);
			response.getWriter().write(fromObject.toString());
		}else if("selects".equals(method)){	//获取选择的投票项目
			String subjectid = request.getParameter("subjectid");
			List<SubItemCustom> allSubject = vDaoImpl.getSelects(Integer.parseInt(subjectid));
			JSONArray fromObject = JSONArray.fromObject(allSubject);
			response.getWriter().write(fromObject.toString());
		}else if("countVote".equals(method)){	//统计投票
			String selectid = request.getParameter("selectid");
			String subjectid = request.getParameter("subjectid");
			vDaoImpl.countVote(Integer.parseInt(selectid));
			List<SubItemCustom> selects = vDaoImpl.getSelects(Integer.parseInt(subjectid));
			JSONArray fromObject = JSONArray.fromObject(selects);
			response.getWriter().write(fromObject.toString());
		}else if("search".equals(method)){	//搜索
			String name = request.getParameter("title");
			List<Subject> searchVotes = vDaoImpl.search(name);
			JSONArray fromObject = JSONArray.fromObject(searchVotes);
			response.getWriter().write(fromObject.toString());
		}
	}

}
