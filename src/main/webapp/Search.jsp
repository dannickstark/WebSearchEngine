<%@ page import="DB.Entities.SearchResult" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="DB.Entities.DocumentEntity" %>


<%@ include file="sections/header.jsp" %>

<script src="https://cdn.tailwindcss.com"></script>

<div class="flex p-2 sticky w-full items-center bg-gray-200 gap-2">
    <a href="./"><img class="h-14" src="./images/logo.png"></a>
    <div class="flex grow">
        <ul class="grow flex items-center gap-4">
            <li>
                <a class="active" aria-current="page" href="./">Home</a>
            </li>
            <li>
                <a class="" href="#">Description</a>
            </li>
        </ul>
        <div class="flex items-center grow border rounded-full bg-white hover:shadow-md">
            <form class="d-flex w-full p-1 gap-2" action="./search">
                <input name="query" id="search-input" value="<c:out value="${query}"></c:out>" type="search" class="grow bg-transparent rounded-full py-[14px] outline-none h-10"/>
                <select name="language" class="form-select form-select-sm rounded-full outline-none w-28" aria-label=".form-select-lg">
                    <option value="en" <c:if test="${language == 'en' || empty language}">selected</c:if>>English</option>
                    <option value="de" <c:if test="${language == 'de'}">selected</c:if>>Deutsch</option>
                </select>
                <select name="type" class="form-select form-select-sm rounded-full outline-none w-28" aria-label=".form-select-lg">
                    <option value="documents" <c:if test="${type == 'documents' || empty language}">selected</c:if>>Documents</option>
                    <option value="images" <c:if test="${type == 'images'}">selected</c:if>>Images</option>
                </select>
                <button submit class="rounded-full bg-cyan-800 text-amber-200 w-10 h-10 flex justify-center items-center">
                    <iconify-icon id="search-btn" icon="ic:baseline-search"></iconify-icon>
                </button>
            </form>
        </div>
    </div>
</div>

<main class="container mb-5 pt-5">
    <p><small>
        About

        <c:choose>
            <c:when test="${type == 'images'}">
                <c:out value="${imageResults.size()}"></c:out>
            </c:when>
            <c:otherwise>
                <c:out value="${results.size()}"></c:out>
            </c:otherwise>
        </c:choose>

        result(s)
        (<c:out value="${elapsedTime}"></c:out> seconds)
    </small></p>

    <c:if test="${not empty alternativeQ}">
        <p><span class="text-base">
            An alternative research could be: <a
                class="text-emerald-600 underline decoration-sky-500"
                href="./search?query=<c:out value="${alternativeQ}"></c:out><c:if test="${not empty language}">&language=<c:out value="${language}"></c:out></c:if><c:if test="${not empty type}">&type=<c:out value="${type}"></c:out></c:if>">
                <c:out value="${alternativeQ}"></c:out>
            </a>
        </span></p>
    </c:if>


    <c:choose>
        <c:when test="${type == 'images'}">
            <div class="flex flex-wrap gap-4 mx-auto pt-2">
                <c:forEach items="${imageResults}" var="result">
                    <a href="<c:out value="${result.docUrl}">#</c:out>" class="card-link" target="_blank">
                        <div class="flex flex-col w-52 gap-2 cursor-pointer">
                            <div class="w-52 h-52 rounded-lg drop-shadow-lg hover:shadow-2xl bg-cover bg-center ..." style='background-image: url(<c:out value="${result.url}">#</c:out>)'></div>
                            <div class="flex flex-col w-full">
                                <span class="font-light text-sm text-cyan-600">Score: <span class="decoration-pink-500 truncate ..."><c:out value="${result.score}"></c:out></span></span>
                                <span class="font-medium text-base text-sky-600 overline truncate ..."><c:out value="${result.docTitle}"></c:out></span>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <ul class="list-group list-group-flush">
                <c:forEach items="${results}" var="result">
                    <li class="list-group-item">
                        <div class="">
                            <div class="card-body">
                                <h5 class="font-medium text-sky-600">
                                    <a href="<c:out value="${result.url}">#</c:out>" class="text-lg text-cyan-800" target="_blank">
                                        <c:out value="${result.title}">...</c:out>
                                    </a>
                                </h5>
                                <c:if test="${!result.internal || (result.internal && isInNetwork) }">
                                    <div class="text-sky-300 text-sm"><c:out value="${result.url}"></c:out></div>
                                    <p class="card-text"><c:out value="${result.description}" escapeXml="false"></c:out></p>
                                </c:if>
                                <div class="flex flex-col mt-3 text-sm text-zinc-500">
                                    <span>Score: <c:out value="${result.score}"></c:out></span>
                                    <c:if test="${not empty result.missingTerms}">
                                        <span class="text-base">
                                            The following term(s) are not present:
                                            <c:forEach items="${result.missingTerms}" var="missingTerm">
                                                <a
                                                        class="text-emerald-600 underline decoration-sky-500"
                                                        href="./search?query=<c:out value="${missingTerm.alternativeQuery}"></c:out><c:if test="${not empty language}">&language=<c:out value="${language}"></c:out></c:if><c:if test="${not empty type}">&type=<c:out value="${type}"></c:out></c:if>">
                                                    <c:out value="${missingTerm.term}"></c:out>
                                                </a>
                                                ,
                                            </c:forEach>
                                        </span>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

</main>
<%@ include file="sections/footer.jsp" %>

<%@ include file="sections/bottom.jsp" %>
