<%@ include file="sections/header.jsp" %>

<script src="https://cdn.tailwindcss.com"></script>

<div class="flex flex-col items-center justify-center h-screen">
    <div class="mb-6">
        <img src="./images/logo.png" alt="Rapide Logo" class="object-cover h-32" />
    </div>
    <div class="flex items-center w-full mx-auto mb-4 border rounded-full lg:max-w-2xl hover:shadow-md">
        <form class="d-flex w-full p-1 gap-2" action="./search">
            <input name="query" id="search-input" type="search" class="grow bg-transparent rounded-full py-[14px] outline-none h-10"/>
            <select name="language" class="form-select form-select-sm rounded-full outline-none w-28" aria-label=".form-select-lg">
                <option value="en">English</option>
                <option value="de">Deutsch</option>
            </select>
            <select name="type" class="form-select form-select-sm rounded-full outline-none w-28" aria-label=".form-select-lg">
                <option value="documents">Documents</option>
                <option value="images">Images</option>
            </select>
            <button submit class="rounded-full bg-cyan-800 text-amber-200 w-10 h-10 flex justify-center items-center">
                <iconify-icon id="search-btn" icon="ic:baseline-search"></iconify-icon>
            </button>
        </form>
    </div>
</div>

<%@ include file="sections/bottom.jsp" %>
