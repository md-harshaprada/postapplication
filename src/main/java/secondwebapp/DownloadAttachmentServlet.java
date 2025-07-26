package secondwebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet("/downloadAttachment")
public class DownloadAttachmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String postIdStr = request.getParameter("postId");
		if (postIdStr != null) {
			try {
				int postId = Integer.parseInt(postIdStr);
				PostService postService = new PostService();
				Post post = postService.getPostById(postId);

				if (post != null && post.getAttachmentFilename() != null) {
					String filename = post.getAttachmentFilename();
					String filetype = post.getAttachmentFiletype();
					InputStream fileData = post.getAttachmentData();

					if (fileData != null) {
						response.setContentType(filetype);
						response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

						try (OutputStream out = response.getOutputStream()) {
							byte[] buffer = new byte[4096];
							int bytesRead;
							while ((bytesRead = fileData.read(buffer)) != -1) {
								out.write(buffer, 0, bytesRead);
							}
						}
					} else {
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment data not found");
					}
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Post or attachment not found");
				}
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID");
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
		}
	}
}
