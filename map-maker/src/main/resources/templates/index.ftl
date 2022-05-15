<!-- ${buildInfo?html} -->
<html lang="en">
<head>
    <title>Dragons Domain IV MUD area maps</title>
    <meta charset="UTF-8">
    <style type="text/css">
        @import url('https://fonts.googleapis.com/css?family=Almendra:400|Metamorphous:400,700|Open+Sans:400,600');

        html {
            margin: 0;
            padding: 0;
        }

        body {
            border: 0;
            padding: 0;
            margin: 3em 4em;
            font-family: 'Open Sans', sans-serif;
            font-size: 14px;
            color: #111111;
            background: linear-gradient(to bottom, #f4f2eb, #cac7bc);
        }

        h1 {
            font-family: 'Metamorphous', cursive;
            color: #666666;
            margin-top: 0;
            margin-bottom: 0.75em;
        }

        h2 {
            font-family: 'Almendra', cursive;
            color: #424242;
            margin: 0 0 0.5em;
        }

        h2 a {
            text-decoration: none;
            color: #555555;
        }

        h2 a:hover {
            text-decoration: underline;
            color: #ef9b14;
        }

        .items {
            margin: 40px 0;
            display: flex;
            flex-direction: row;
            flex-wrap: wrap;
            align-content: flex-start;
            justify-content: flex-start;
            align-items: flex-start;
            gap: 20px 25px;
        }

        .item {
            width: 350px;
            box-sizing: border-box;
            border: 1px solid #949494;
            background: #fafafa;
            border-radius: 4px;
            padding: 15px;
            break-inside: avoid;
        }

        .details {
            color: #666666;
        }
    </style>
</head>
<body>
<h1>Dragons Domain IV MUD area maps</h1>
<div class="items">
    <#list items as item>
        <div class="item">
            <h2><a href="${item.link?html}">${item.name?html}</a></h2>
            <div class="details">
                ${item.levelDescription?html} &mdash; ${item.author?html}
            </div>
        </div>
    </#list>
</div>
</body>
</html>
