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
            font-size: 15px;
            color: #111111;
            background: #efede6;
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
            columns: auto;
            column-width: 300px;
            column-gap: 40px;
            margin: 40px 0;
            column-fill: balance;
        }

        .item {
            box-sizing: border-box;
            border: 1px solid #949494;
            background: #fafafa;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
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
