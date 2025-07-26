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
	        
	        var sessionDetailsContent = document.getElementById('sessionDetailsContent');
	        var isVisible = sessionDetailsContent.style.display !== 'none';
var sessionId = sessionDetailsContent.getAttribute('data-session-id');
	        localStorage.setItem('isCassandra', isCassandra);
	        $('#isCassandraField').val(isCassandra);

	        if (isVisible) {
	            window.location.href = 'groupsessions.jsp?sessionId=' + sessionId + '&isCassandra=' + isCassandra;
	          } else {
	            window.location.href = 'groupsessions.jsp?isCassandra=' + isCassandra;
	          }
	    });
	    $('#postForm').on('submit', function() {
	        updateIsCassandraField();
	    });
	 document.getElementById('answer-form').addEventListener('submit', function(event) {
		 const isCassandra = localStorage.getItem('isCassandra') === 'true';
		 document.getElementById('isCassandraField-answer').value = isCassandra ? 'true' : 'false';
	 });
	    updateIsCassandraField();
	});
    $(document).ready(function() {
        const urlParams = new URLSearchParams(window.location.search);
        const sessionId = urlParams.get('sessionId');

        if (sessionId) {
            $('#createSessionContent').hide();
            $('#activeSessionsContent').hide();
            $('#sessionDetailsContent').show();
        } else {
            $('#createSessionContent').show();
            $('#activeSessionsContent').hide();
            $('#sessionDetailsContent').hide();
        }

        $('#createSessionLink').click(function() {
            $('#createSessionContent').show();
            $('#activeSessionsContent').hide();
            $('#sessionDetailsContent').hide();
        });

        $('#activeSessionsLink').click(function() {
            $('#createSessionContent').hide();
            $('#activeSessionsContent').show();
            $('#sessionDetailsContent').hide();
        });

        $('.session-link').click(function(event) {
            event.preventDefault();
            const sessionId = $(this).data('session-id');
            window.location.href = 'groupsessions.jsp?sessionId=' + sessionId;
        });
    });
	function upvoteAnswer(answerId, sessionId,questionId) {
	    fetch('postupvotes?answerId=' + answerId + '&sessionId=' + sessionId + '&questionId=' + questionId)
	        .then(response => response.text())
	        .then(data => {
	            document.getElementById('upvote-count-answer-' + answerId).innerText = data + " Upvotes";
	        });
	}
	
	$(document).ready(function() {
	    $('.show-answers').click(function(event) {
	        event.preventDefault();
	        const questionId = $(this).data('question-id');
	        $('#answer-list-' + questionId).toggle();
	    });

	    $('.reply-toggle').click(function(event) {
	        event.preventDefault();
	        const questionId = $(this).data('question-id');
	        $('#answer-form-' + questionId).toggle();
	    });
	});
	
	function toggleSpecificUsers() {
	    var share = document.getElementById("admin").value;
	    var specificUsersDiv = document.getElementById("specific-users");
	    specificUsersDiv.style.display = (share === "specific") ? "block" : "none";
	}

	function updateGroupMembers() {
	    var groupId = document.getElementById("group").value;

	    if (groupId) {
	        fetch('createSession?groupId=' + groupId) 
	            .then(response => response.json())
	            .then(data => {
	                var userCheckboxes = document.getElementById("user-checkboxes");
	                userCheckboxes.innerHTML = '';
	                data.forEach(user => {
	                        userCheckboxes.innerHTML +=
	                            '<div class="form-check form-check-inline">' +
	                            '    <input type="checkbox" name="specificUsers" value="' + user + '" class="form-check-input">' +
	                            '    <label class="form-check-label">' + user + '</label>' +
	                            '</div>';
	                });
	            })
	            .catch(error => console.error('Error fetching group members:', error));
	    } else {
	        var userCheckboxes = document.getElementById("user-checkboxes");
	        userCheckboxes.innerHTML = '';
	    }
	}