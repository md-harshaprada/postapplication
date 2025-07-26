function showUserSelection() {
	document.getElementById('user-selection').style.display = 'block';
}

$(function() {
	const updateIsCassandraField = () => {
		const isCassandra = localStorage.getItem('isCassandra') === 'true';
		$('#isCassandraField').val(isCassandra ? 'true' : 'false');
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

		window.location.href = 'group.jsp?isCassandra=' + isCassandra;
	});
	$('#postForm').on('submit', function() {
		updateIsCassandraField();
	});
	updateIsCassandraField();
});
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