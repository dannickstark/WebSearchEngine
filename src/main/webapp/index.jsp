<%@ include file="sections/header.jsp" %>

<script src="https://cdn.tailwindcss.com"></script>

<div class="flex flex-col items-center justify-center h-screen">
    <div class="mb-6">
        <img src="./images/logo.png" alt="Rapide Logo" class="object-cover h-32" />
    </div>
    <div class="flex items-center w-full mx-auto mb-4 border rounded-full lg:max-w-2xl hover:shadow-md">
        <input id="search-input" type="text" class="w-full bg-transparent rounded-full py-[14px] pl-4 outline-none"/>
        <div class="pr-5 cursor-pointer">
            <iconify-icon id="search-btn" icon="ic:baseline-search"></iconify-icon>
        </div>
    </div>
</div>
<%@ include file="sections/bottom.jsp" %>
