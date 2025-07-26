$(function() {

	const updateIsCassandraField = () => {
		const isCassandra1 = localStorage.getItem('isCassandra') === 'true';
		$('#isCassandraField').val(isCassandra1 ? 'true' : 'false');
	};

	const isCassandra = localStorage.getItem('isCassandra') === 'true';
	$('#toggle-two').bootstrapToggle(isCassandra ? 'on' : 'off');

	$('#toggle-two').change(function() {
		const isChecked = $(this).prop('checked');
		const isCassandra = isChecked ? 'true' : 'false';

		localStorage.setItem('isCassandra', isCassandra);
		$('#isCassandraField').val(isCassandra);

		console.log('isCassandra:', isCassandra);
		console.log('Hidden field value:', $('#isCassandraField').val());

		window.location.href = 'dashboard.jsp?isCassandra=' + isCassandra;
	});

	$('#postForm').on('submit', function() {
		updateIsCassandraField();
	});

	document.getElementById('reply-form').addEventListener('submit', function(event) {

		const isCassandra1 = localStorage.getItem('isCassandra') === 'true';
	            document.getElementById('isCassandraField1').value = isCassandra1 ? 'true' : 'false';
	        });
	updateIsCassandraField();
});

function toggleSpecificUsers() {
	var share = document.getElementById("share").value;
	var specificUsersDiv = document.getElementById("specific-users");
	specificUsersDiv.style.display = (share === "specific") ? "block" : "none";
}

function likePost(postId) {
	fetch('likePost?postId=' + postId)
		.then(response => response.text())
		.then(data => {
			document.getElementById('like-count-post-' + postId).innerText = data + " Likes";
		});
}

function likeReply(replyId) {
	fetch('likeReply?replyId=' + replyId)
		.then(response => response.text())
		.then(data => {
			document.getElementById('like-count-reply-' + replyId).innerText = data + " Likes";
		});
}
function toggleContent(type, id) {
	const shortContent = document.getElementById('short-' + type + '-' + id);
	const fullContent = document.getElementById('full-' + type + '-' + id);
	const seeMoreLink = document.getElementById('see-more-' + type + '-' + id);
	const seeLessLink = document.getElementById('see-less-' + type + '-' + id);

	if (shortContent.style.display === 'none') {
		shortContent.style.display = 'inline';
		fullContent.style.display = 'none';
		seeMoreLink.style.display = 'inline';
		seeLessLink.style.display = 'none';
	} else {
		shortContent.style.display = 'none';
		fullContent.style.display = 'inline';
		seeMoreLink.style.display = 'none';
		seeLessLink.style.display = 'inline';
	}
}

function toggleReplies(replyId) {
    var replyList = document.getElementById('reply-list-' + replyId);
    var toggleLink = document.getElementById('toggle-replies-link-' + replyId);
    if (replyList.style.display === 'none') {
        replyList.style.display = 'block';
        toggleLink.textContent = 'Hide Replies';
    } else {
        replyList.style.display = 'none';
        toggleLink.textContent = 'Show Replies';
    }
}

function toggleReplyForm(replyId) {
    var replyForm = document.getElementById('reply-form-' + replyId);
    var toggleLink = document.getElementById('toggle-reply-form-link-' + replyId);
    if (replyForm.style.display === 'none') {
        replyForm.style.display = 'block';
        toggleLink.textContent = 'Hide';
    } else {
        replyForm.style.display = 'none';
        toggleLink.textContent = 'Reply';
    }
}
