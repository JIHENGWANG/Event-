package rpc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Originl", "*");
		response.setContentType("application/json");
		
		String name = "";
		if(request.getParameter("username") != null) {
			name = request.getParameter("username");
		}
		JSONArray array = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
			obj.put("username", "abcd");
			obj.put("address", "China");
			obj.put("course", "fuck");
			array.put(obj);
			JSONObject obj2 = new JSONObject();
			obj2.put("username", "asdf");
			obj2.put("address", "bee");
			obj2.put("course", "low");
			array.put(obj2);
			//obj.put("username", name);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.print(array);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
