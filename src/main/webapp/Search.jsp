<%@ page import="DB.Entities.SearchResult" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="DB.Entities.DocumentEntity" %>


<%@ include file="sections/header.jsp" %>
<nav class="navbar navbar-expand-md fixed-top navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="/"><img src="./images/logo.png" height="45"></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <ul class="navbar-nav me-auto mb-2 mb-md-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Description</a>
                </li>
            </ul>
            <form action="/search" class="d-flex left-zone-bar">
                <input name="q" value="<c:out value="${query}"></c:out>" class="form-control" type="search" placeholder="Search" aria-label="Search">
                <select name="language" class="form-select" aria-label="Select the language">
                    <option value="en" <c:if test="${language == 'en' || empty language}">selected</c:if>>English</option>
                    <option value="de" <c:if test="${language == 'de'}">selected</c:if>>Deutsch</option>
                </select>
                <button class="btn btn-outline-success" type="submit">Search</button>
            </form>
        </div>
    </div>
</nav>

<main class="container mt-5 mb-5 pt-5">
    <p><small>
        About <c:out value="${results.size()}"></c:out> result(s)
        (<c:out value="${elapsedTime}"></c:out> seconds)
    </small></p>

    <c:if test="${not empty alternativeQ}">
        <p><small>
            An alternative research could be <a href="/search?q=<c:out value="${alternativeQ}"></c:out>"><c:out value="${alternativeQ}"></c:out></a>
        </small></p>
    </c:if>

    <ul class="list-group list-group-flush">
        <c:forEach items="${results}" var="result">
            <li class="list-group-item">
                <div class="">
                    <div class="card-body">
                        <h5 class="card-title">
                            <a href="<c:out value="${result.url}">#</c:out>" class="card-link">
                                <c:out value="${result.title}">...</c:out>
                            </a>
                        </h5
                            <c:if test="${!result.internal || (result.internal && isInNetwork) }">
                                <h6 class="card-subtitle mb-2 text-muted"><c:out value="${result.url}"></c:out></h6>
                                <p class="card-text"><c:out value="${result.description}"></c:out></p>
                            </c:if>
                    </div>
                </div>
            </li>
        </c:forEach>
    </ul>
</main>
<%@ include file="sections/footer.jsp" %>
<%@ include file="sections/bottom.jsp" %>
