package io.recommender.project.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.recommender.project.engine.RecommenderEngine;
import io.recommender.project.forms.Post;
import io.recommender.project.model.MovieRepository;
import io.recommender.project.model.Movies;
import io.recommender.project.model.UserAccount;
import io.recommender.project.model.UserAccountRepository;

@Controller
public class ProjectController {

	@Autowired
	private RecommenderEngine engine;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private UserAccountRepository accoutRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Post post) {
		return "index";
	}

	@RequestMapping("/login")
	public String getLoginAccess(Post post) {
		return "redirect:/";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String logIn(@Valid Post post, BindingResult bindingResult, Model model, HttpSession session)
			throws TasteException {
		if (bindingResult.hasErrors()) {
			return "index";
		}

		UserAccount user = accoutRepository.findOne(post.getUserName());
		if (user != null && user.getPassword().equals(post.getPassword())) {
			session.setAttribute("name", post.getUserName());
			session.setAttribute("user_id", user.getUser_id());
			return "redirect:/getRecommendations";
		} else {
			model.addAttribute("invalidCridential", true);
			return "index";
		}

	}

	@RequestMapping("/getRecommendations")
	public String getRecommendation(Model model, Post post, HttpSession session) throws TasteException {

		if (session.getAttribute("name") == null) {
			return "redirect:/";
		} else {

			int id = (int) session.getAttribute("user_id");
			Map<String, Float> est_rates = new HashMap<String, Float>();
			List<RecommendedItem> id_rating = engine.getRecommendation(id, 30);

			id_rating.forEach(item -> est_rates.put(item.getItemID() + "", item.getValue()));
			List<Movies> items = getItems(id_rating);

			model.addAttribute("items", items);
			model.addAttribute("Est_Rate", est_rates);
			return "recommendeditems";
		}

	}

	@RequestMapping("logout")
	public String logOut(Post post, HttpSession session) {
		//session.removeAttribute("name");
		session.invalidate();
		return "redirect:/";
	}

	/*
	 * Helper function it is bad practice but for now it is okay
	 */
	public List<Movies> getItems(List<RecommendedItem> items) throws TasteException {

		List<Movies> movies = new ArrayList<Movies>();
		items.forEach(item -> movies.add(movieRepository.findOne((int) item.getItemID())));
		return movies;
	}
}
