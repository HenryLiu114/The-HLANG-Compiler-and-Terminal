function navbar() {
    return `
    <div class="navbar">
        <a href="../"><img class="homebutton" src="../imgs/SimpleLogo.png"></a>
        <a href="../downloadpage/" class="selectionbuttons">Downloads</a>
        <a href="../changelogpage/" class="selectionbuttons">Changelog</a>
        <a href="../manualpage/" class="selectionbuttons">Manuals</a>
        <a href="../aboutpage/" class="selectionbuttons">About</a>
        <a href="https://github.com/HenryLiu114/The-HLANG-Compiler-and-Terminal" class="selectionbuttons">Github</a>
        <img class="tabbutton" src="../imgs/bars-solid-full.svg">
    </div>
    `;
}

function dropdown(categorys, h1, h2) {
    let string = "";
    let curpos = 0;

    for (let i = 0; i < categorys.length; i++) {

        string += `<div class="header3">${categorys[i]}
        </div>`;
        string += `<div class="innerdropdown">`;
        for (let j = 0; j < h1[i].length; j++) {
            string += `<div class="header4">${h1[i][j]}</div>`;
            string += `<div class="header5">${h2[curpos]}</div>`;
            curpos++;
        }

        string += `</div>`;
    }

    console.log(string);

    return string;
}

function reservedWords() {
    categorys = [
        "Two Param Arithmetic",
        "One Param Arithmetic",
        "Output",
        "Variable Declaration",
        "Two Param Logical",
        "One Param Logical",
        "Conditional Statements",
        "If Statements",
        "Function Declaration and Calls",
        "List Special Fnctions",
        "Input",
        "Mapcar",
        "Wildcard",
        "Return Stack",
        "Loops"
    ];

    h1 = [
        // Two Param Arithmetic
        [
            "/add &lt;x1&gt; &lt;x2&gt;.",
            "/sub &lt;x1&gt; &lt;x2&gt;.",
            "/mul &lt;x1&gt; &lt;x2&gt;.",
            "/div &lt;x1&gt; &lt;x2&gt;.",
            "/mod &lt;x1&gt; &lt;x2&gt;.",
            "/pow &lt;x1&gt; &lt;x2&gt;."
        ],

        // One Param Arithmetic
        ["/abs &lt;x1&gt;."],

        // Output
        [
            "/print &lt;token&gt;.",
            "/println &lt;token&gt;."
        ],

        // Variable Declaration
        ["/var &lt;name&gt; &lt;token&gt;."],

        // Two Param Logical
        [
            "/and &lt;x1&gt; &lt;x2&gt;.",
            "/or &lt;x1&gt; &lt;x2&gt;."
        ],

        // One Param Logical
        ["/not &lt;x1&gt;."],

        // Conditional Statements
        [
            "/gteq &lt;x1&gt; &lt;x2&gt;.",
            "/gt &lt;x1&gt; &lt;x2&gt;.",
            "/lteq &lt;x1&gt; &lt;x2&gt;.",
            "/lt &lt;x1&gt; &lt;x2&gt;.",
            "/eq &lt;x1&gt; &lt;x2&gt;.",
            "/neq &lt;x1&gt; &lt;x2&gt;."
        ],

        // If Statements
        ["/if (&lt;condition&gt;) {&lt;trueBranch&gt;} {&lt;falseBranch&gt;}. "],

        // Function Declaration and Calls
        [
            "/usefun &lt;functName&gt; (&lt;parameters&gt;).",
            "/defun &lt;functName&gt; (&lt;parameters&gt;) {&lt;operation&gt;}."
        ],

        // List Special Functions
        [
            "/list &lt;name&gt; [&lt;list&gt;].",
            "/cons",
            "/cdr &lt;list&gt; &lt;variable&gt;.",
            "/car &lt;list&gt; &lt;variable&gt;.",
            "/isempty &lt;list&gt;.",
            "/copyl &lt;list1&gt; &lt;variable&gt;."
        ],

        // Input
        ["/prompt &lt;string&gt; &lt;variable&gt;."],

        // Mapcar
        ["/mapcar"],

        // Wildcard
        ["/?"],

        // Return Stack
        [
            "/ret &lt;data&gt;.",
            "/rel &lt;data&gt;."
        ],

        //Loops
        [
            "/while (&lt;condition&gt;) { &lt;todo&gt; }.",
            "/for &lt;variableName&gt; &lt;startingPoint&gt; &lt;endingPoint&gt; &lt;incrementNumber&gt; { &lt;todo&gt; }."
        ]
    ];

    h2 = [
        // add
        "x1 + x2",

        // sub
        "x1 - x2",

        // mul
        "x1 * x2",

        // div
        "x1 / x2",

        // mod
        "x1 % x2",

        // pow
        "x1 ^ x2",

        // abs
        "Absolute value of x1",

        // print
        "Prints the &lt;token&gt;",

        // println
        "Prints the &lt;token&gt; with an extra line",

        // var
        "Sets &lt;name&gt;'s data as &lt;token&gt;. " +
        "Type inference is applied. You may also " +
        "declare a list using /var.",

        // and
        "Finds the and of &lt;x1&gt; and &lt;x2&gt;. &lt;x1&gt; and &lt;x2&gt; " +
        "must be bools.",

        // or
        "Finds the or of &lt;x1&gt; and &lt;x2&gt;. &lt;x1&gt; and &lt;x2&gt; " +
        "must be bools.",

        // not
        "Finds the not of &lt;x1&gt;. &lt;x1&gt; must be a bool.",

        // gteq
        "Checks if &lt;x1&gt; is greater than or equal to &lt;x2&gt;. Type inference is applied.",

        // gt
        "Checks if &lt;x1&gt; is greater than &lt;x2&gt;. Type inference is applied.",

        // lteq
        "Checks if &lt;x1&gt; is less than or equal to &lt;x2&gt;. Type inference is applied.",

        // lt
        "Check if &lt;x1&gt; is less than &lt;x2&gt;. Type inference is applied.",

        // eq
        "Checks if &lt;x1&gt; is equal to &lt;x2&gt;. Type inference is applied.",

        // neq
        "Checks if &lt;x1&gt; is not equal to &lt;x2&gt;. Type inference is applied.",

        // if
        "Standard if statement.",

        // Usefun
        "Calls a function by &lt;functionName&gt; with &lt;parameters&gt;. Note: different &lt;parameters&gt; are spaced out using a space “ “.",

        // Defun
        "Creates a new function with &lt;functName&gt;. Note: different &lt;parameters&gt; are spaced out using a space “ “.",

        // list
        "Another way to declare a list without /var. Potentially remove in the future.",

        // cons
        "Does absolutely nothing, plan to remove.",

        // cdr
        "Stores the tail of the &lt;list&gt; in &lt;variable&gt;",

        // car
        "Stores the head of the &lt;list&gt; in &lt;variable&gt;.",

        // isempty
        "Checks if a list is empty.",

        // copyl
        "Make a hard copy of &lt;list1&gt; on &lt;variable&gt;.",

        // Input
        "Prints a &lt;string&gt; and stores user input to &lt;variable&gt;.",

        // mapcar
        "Does nothing right now.",

        // Wildcard
        "Wildcard Character",

        // Ret
        "Put the data into Return Stack.",

        // rel
        "Retrieves data from the return stack.",

        // while
        "Loops until condition is met. Real men use recursion.",

        // for
        "Loops a preset amount of time until number is reached. Loops are for babies."
    ];

    return dropdown(categorys, h1, h2);
}

function specialcases() {
    categorys = [
        "/add",
        "/sub",
        "/mul",
        "/div",
        "/mod"
    ];

    h1 = [
        [
            "string + anything = concatenation from the back",
            "anything + string = concatenation from the front",
            "list + anything = Add to back",
            "anything + list = Add to front",
            "list + list = Combines two lists"
        ],

        [
            "string - integer = remove character at integer index",
            "integer - string = remove all characters but the one at integer index",
            "list - integer = removes the item at integer index from list",
            "integer - list = retrieve the item at index from list",
        ],

        [
            "string * integer = Duplicates the string integer times",
            "integer * string = The first integer characters",
            "string1 * string2 = number of occurrences of string2 in string1",
            "string1 * wildcard = length of string",
            "list * anything = looks for the first occurrence of this item",
            "anything * list = check if the item exists",
            "list * wildcard = length of list alternative to /listlen"
        ],

        [
            "string / int = substring from 0 to int",
            "int / string = substring from int to length",
            "string / string = basically remove dupes",
            "int / list = item from index of list",
            "list / list = the Not Intersection of 2 Lists"
        ],

        ["list % list = the Intersection of 2 lists"]
    ];

    h2 = [
        "","","","","",
        "","","","",
        "","","","","","","",
        "","","","","",
        "",
    ];

    return dropdown(categorys, h1, h2);
}

function syntaxstuff() {
    categorys = [
        "Format of HLANG commands",
        "Token Types",
        "Comments"
    ];

    h1 = [
        ["Must start with a \"/\" which denotes a command. End each command with a \".\""],
        ["Integer", "Floating", "Bool", "String", "List"],
        ["Comments are single line only and are denoted with a #."]
    ];

    h2 = [
        "",
        //Integer
        "An 32 bit signed value that represents nondecimal values. To make one just type a number from -2147483648 to 2147483647.",

        //Floating
        "A 64 bit signed value that represents decimal values To make a floating point value, you must use \"_\" to represent a decimal point. Example: 1_5 = 1.5 or 3_14 = 3.14.",

        //Bool
        "True or False. Declare using T or NIL.",

        //String
        "A set of characters. Please use quotations to declare a string. To add quotes inside of a string, use \"\\\"\". To add backslashes inside of a string, use \"\\\\\". To add a # to a string, use \"\\#\".",

        //List
        "A collection of tokens. HLANG Lists do not care about types, allowing you to make tuples. To make a list, use [] separate indexes using \",\".",

        //Comment
        "# This is a comment!"
    ];

    return dropdown(categorys, h1, h2);
}

const dropdownBtn = document.getElementById("dropdownBtn1");
const dropdownMenu = document.getElementById("reservedwords");

const dropdownBtn2 = document.getElementById("dropdownbtn2");
const dropdownMenu2 = document.getElementById("syntaxstuff");

const dropdownBtn3 = document.getElementById("dropdownbtn3");
const dropdownMenu3 = document.getElementById("specialcases");

// First dropdown button
dropdownBtn.addEventListener("click", function (event) {
    event.stopPropagation();

    dropdownMenu.classList.toggle("show");
    dropdownBtn.classList.toggle("show");
});

// Second dropdown button
dropdownBtn2.addEventListener("click", function (event) {
    event.stopPropagation();

    dropdownMenu2.classList.toggle("show");
    dropdownBtn2.classList.toggle("show");
});

// Third dropdown button
dropdownBtn3.addEventListener("click", function (event) {
    event.stopPropagation();

    dropdownMenu3.classList.toggle("show");
    dropdownBtn3.classList.toggle("show");
});

// Close dropdowns when clicking outside
window.addEventListener("click", function (event) {
    if (!dropdownBtn.contains(event.target) &&
        !dropdownMenu.contains(event.target)) {
        dropdownMenu.classList.remove("show");
        dropdownBtn.classList.remove("show");
    }

    if (!dropdownBtn2.contains(event.target) &&
        !dropdownMenu2.contains(event.target)) {
        dropdownMenu2.classList.remove("show");
        dropdownBtn2.classList.remove("show");
    }

    if (!dropdownBtn3.contains(event.target) &&
        !dropdownMenu3.contains(event.target)) {
        dropdownMenu3.classList.remove("show");
        dropdownBtn3.classList.remove("show");
    }
});