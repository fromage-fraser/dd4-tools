<#assign cellsBorderWidth = "30px">
<#assign cellBorderWidth = "6px">
<#assign cellSideLength = "108px">
<#assign cellColourEmpty = "#cccccc">
<#assign cellColourBackground = "#ffffff">
<#assign fragmentColourBorder = "#999999">
<#assign fragmentColourBackground = "#ffffff">
<#assign sectorColorUnknown = "#999999">
<#assign roomColourForeground = "#333333">
<#assign roomColourBackground = "#ffffff">
<#assign doorColour = "#d8c4b8">
<#assign doorColourBorder = "#6b5130">
<#assign doorBorderWidth = "2px">
<#assign doorMarginFront = "-4px">
<#assign doorMarginSide = "12px">
<#assign doorMarginRear = "-1px">
<#assign areaExitColourBorder = "#ff530d">
<#assign areaExitColourForeground = "#b13808">
<#assign areaExitColourBackground = "#ffffff">
<#assign jumpColourSource = "#fb8403">
<#assign jumpColourTarget = "#cc4114">
<#assign styleCell = "a">
<#assign styleEmptyCell = "b">
<#assign styleRoomCell = "c">
<#assign styleRoomMain = "d">
<#assign styleRoomFlag = "e">
<#assign styleCellLink = "f">
<#assign styleCellEdge = "g">
<#assign styleCellEdgeType = "h">
<#assign styleCellLinkTypeJump = "i">
<#assign styleCellLinkTypeLink = "j">
<#assign styleCellLinkTypeUnlinked = "k">
<#assign styleCellMainContent = "l">
<#assign styleArrowDirection = "m">
<#assign styleSector = "n">
<#assign styleNorthSouthConnector = "o">
<#assign styleEastWestConnector = "p">
<#assign styleLegendSymbol = "q">
<#assign styleLegendDescription = "r">
<#assign styleLegendSector = "s">
<#assign styleConnectorCell = "t">
<#assign styleRoomLabel = "u">
<!-- ${buildInfo?html} -->
<html lang="en">
<head>
    <title>${areaMap.name()?html}</title>
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

        h1, h2, h3 {
            font-family: 'Metamorphous', cursive;
            color: #555555;
            margin-top: 0;
            margin-bottom: 0.75em;
        }

        a {
            color: #333333;
            text-decoration: none;
        }

        a:hover {
            color: #f69700;
            text-decoration: underline;
        }

        a.invis {
            height: 0;
            width: 0;
        }

        .subheading {
            font-family: 'Almendra', cursive;
            font-size: 1.25em;
        }

        .vnum {
            font-weight: bold;
        }

        .fragments {
            display: flex;
            flex-direction: row;
            flex-wrap: wrap;
            justify-content: flex-start;
            align-content: flex-start;
            align-items: center;
            margin: 15px -15px -15px -15px;
        }

        .fragment {
            border: 1px solid ${fragmentColourBorder};
            border-radius: 8px;
            padding: 15px;
            margin: 15px;
            background: ${fragmentColourBackground};
        }

        .legend {
            border: 1px solid ${fragmentColourBorder};
            border-radius: 8px;
            padding: 15px;
            margin: 15px;
            background: ${cellColourBackground};
            display: grid;
            grid-template-columns: auto auto auto auto;
            grid-gap: 15px;
            font-family: 'Open Sans', sans-serif;
            font-size: 12px;
        }

        .${styleLegendSector} {
            display: inline-block;
            height: 12px;
            width: 12px;
        }

        .${styleLegendSymbol} {
            margin: auto;
        }

        .${styleLegendDescription} {
        }

        .cells {
            border: ${cellsBorderWidth} solid ${cellColourEmpty};
            border-radius: 4px;
            display: grid;
            grid-gap: 1px;
            margin: 0;
        }

        .${styleCell} {
            box-sizing: border-box;
            height: ${cellSideLength};
            width: ${cellSideLength};
        }

        .${styleRoomCell} {
            background: ${cellColourBackground};
            display: grid;
            grid-template-columns: ${cellBorderWidth} 1fr 5fr 1fr ${cellBorderWidth};
            grid-template-rows: ${cellBorderWidth} 1fr 5fr 1fr ${cellBorderWidth};
        }

        .area-exit-cell {
            background: ${areaExitColourBorder};
            display: grid;
            grid-template-columns: ${cellBorderWidth} 1fr 5fr 1fr ${cellBorderWidth};
            grid-template-rows: ${cellBorderWidth} 1fr 5fr 1fr ${cellBorderWidth};
        }

        .${styleConnectorCell} {
            background: ${cellColourEmpty};
            display: grid;
            grid-template-columns: 2fr 2fr 2fr;
            grid-template-rows: 2fr 2fr 2fr;
        }

        .${styleNorthSouthConnector} {
            grid-column: 2;
            grid-row: 1 / 4;
            background: ${cellColourBackground};
            border: 1px solid ${cellColourEmpty};
        }

        .${styleEastWestConnector} {
            grid-row: 2;
            grid-column: 1 / 4;
            background: ${cellColourBackground};
            border: 1px solid ${cellColourEmpty};
        }

        .${styleEmptyCell} {
            background: ${cellColourEmpty};
        }

        .${styleSector}-inside {
            background: #808080;
        }

        .${styleSector}-city {
            background: #aba596;
        }

        .${styleSector}-field {
            background: #b1ba31;
        }

        .${styleSector}-forest {
            background: #6b9d3d;
        }

        .${styleSector}-hills {
            background: #7d995e;
        }

        .${styleSector}-mountain {
            background: #929292;
        }

        .${styleSector}-water_swim {
            background: #59addb;
        }

        .${styleSector}-water_no_swim {
            background: #0c62bd;
        }

        .${styleSector}-underwater {
            background: #082fa9;
        }

        .${styleSector}-air {
            background: #c4d3d7;
        }

        .${styleSector}-desert {
            background: #e2bf11;
        }

        .${styleSector}-swamp {
            background: #6b8552;
        }

        .${styleSector}-underwater_ground {
            background: #324a63;
        }

        .${styleRoomMain} {
            color: ${roomColourForeground};
            background: ${roomColourBackground};
            grid-column: 2 / 5;
            grid-row: 2 / 5;
        }

        .area-exit-main {
            color: ${areaExitColourForeground};
            background: ${areaExitColourBackground};
            grid-column: 2 / 5;
            grid-row: 2 / 5;
        }

        .${styleCellMainContent} {
            text-align: center;
            font-family: 'Open Sans', sans-serif;
            font-size: 12px;
            hyphens: auto;
            margin: 10px;
            overflow: hidden;
        }

        .area-exit-main a:link,
        .area-exit-main a:visited,
        .area-exit-main a:hover,
        .area-exit-main a:active {
            color: ${areaExitColourForeground};
        }

        .${styleCellEdge}-n {
            grid-row: 1;
            grid-column: 3 / 4;
            border-width: 0 ${doorBorderWidth} ${doorBorderWidth} ${doorBorderWidth};
        }

        .${styleCellEdge}-s {
            grid-row: 5;
            grid-column: 3 / 4;
            border-width: ${doorBorderWidth} ${doorBorderWidth} 0 ${doorBorderWidth};
        }

        .${styleCellEdge}-e {
            grid-row: 3 / 4;
            grid-column: 5;
            border-width: ${doorBorderWidth} 0 ${doorBorderWidth} ${doorBorderWidth};
        }

        .${styleCellEdge}-w {
            grid-row: 3 / 4;
            grid-column: 1;
            border-width: ${doorBorderWidth} ${doorBorderWidth} ${doorBorderWidth} 0;
        }

        .${styleCellEdgeType}-open {
            background: ${roomColourBackground};
            border-color: ${roomColourBackground};
        }

        .${styleCellEdgeType}-door-north,
        .${styleCellEdgeType}-door-south,
        .${styleCellEdgeType}-door-east,
        .${styleCellEdgeType}-door-west {
            z-index: 100;
            background: ${doorColour};
            border-color: ${doorColourBorder};
            border-style: solid;
            border-radius: 2px;
        }

        .${styleCellEdgeType}-door-north {
            margin: ${doorMarginRear} ${doorMarginSide} ${doorMarginFront} ${doorMarginSide};
        }

        .${styleCellEdgeType}-door-south {
            margin: ${doorMarginFront} ${doorMarginSide} ${doorMarginRear} ${doorMarginSide};
        }

        .${styleCellEdgeType}-door-east {
            margin: ${doorMarginSide} ${doorMarginRear} ${doorMarginSide} ${doorMarginFront};
        }

        .${styleCellEdgeType}-door-west {
            margin: ${doorMarginSide} ${doorMarginFront} ${doorMarginSide} ${doorMarginRear};
        }

        .${styleCellLink} {
            z-index: 100;
            font-family: monospace;
            font-size: 12px;
            margin: auto;
        }

        .${styleCellLink}-n {
            grid-row: 1  / 4;
            grid-column: 2 / 5;
            margin-top: 1px
        }

        .${styleCellLink}-s {
            grid-row: 3 / 6;
            grid-column: 3 / 4;
            margin-bottom: 1px
        }

        .${styleCellLink}-e {
            grid-row: 2 / 5;
            grid-column: 3 / 6;
            margin-right: 0;
        }

        .${styleCellLink}-w {
            grid-row: 2 / 5;
            grid-column: 1 / 4;
            margin-left: 0
        }

        .${styleCellLink}-u {
            grid-row: 1 / 6;
            grid-column: 1 / 6;
            margin-top: 3px;
            margin-left: 3px;
        }

        .${styleCellLink}-d {
            grid-row: 1 / 6;
            grid-column: 1 / 6;
            margin-right: 3px;
            margin-bottom: 3px;
        }

        .${styleCellLinkTypeUnlinked} {
            background: #ee00ff;
            color: #fdfdfd;
            padding: 1px 4px;
            border-radius: 12px;
        }

        .${styleCellLinkTypeJump} {
            background: ${jumpColourSource};
            color: #fafafa;
            padding: 1px 4px;
            border-radius: 12px;
            cursor: pointer;
        }

        .${styleArrowDirection}-n::before {
            content: "^";
        }

        .${styleArrowDirection}-s::before {
            content: "v";
        }

        .${styleArrowDirection}-e::before {
            content: ">";
        }

        .${styleArrowDirection}-w::before {
            content: "<";
        }

        .${styleArrowDirection}-u::before {
            content: "^";
        }

        .${styleArrowDirection}-d::before {
            content: "v";
        }

        .${styleCellLinkTypeLink} {
            background: #b9c2c7;
            color: #fafafa;
            padding: 1px 4px;
        }

        .room-flags {
            margin-top: 3px;
        }

        .${styleRoomFlag} {
            display: inline-block;
            padding: 1px 4px;
            border-radius: 10px;
            background: #999999;
            color: #f8f8f8;
            font-family: monospace;
            font-size: 11px;
            cursor: pointer;
        }

        .${styleRoomFlag}-randomized-exits::before {
            content: "R";
        }

        .${styleRoomFlag}-no-mobiles {
            background: #1aa8e2 !important;
        }

        .${styleRoomFlag}-no-mobiles::before {
            content: "NM";
        }

        .${styleRoomFlag}-no-recall {
            background: #102e67 !important;
        }

        .${styleRoomFlag}-no-recall::before {
            content: "NR";
        }

        .${styleRoomFlag}-healer {
            background: #2b9611 !important;
        }

        .${styleRoomFlag}-healer::before {
            content: "H";
        }

        .${styleRoomFlag}-shop {
            background: #ffbf00 !important;
            color: #483602 !important;
        }

        .${styleRoomFlag}-shop::before {
            content: "$";
        }


        .${styleRoomFlag}-teacher {
            background: #b516c6 !important;
        }

        .${styleRoomFlag}-teacher::before {
            content: "T";
        }

        .${styleRoomLabel} {
            padding: 1px 4px;
            border-radius: 2px;
            background: ${jumpColourTarget};
            color: #f8f8f8;
            font-family: monospace;
            cursor: pointer;
        }

        .summary .links {
            float: right;
        }

        .summary .links a {
             margin-left: 15px;
        }
    </style>
</head>
<body>
<div class="summary">
    <div class="links">
        <a href="${indexLink?html}">Index</a>
    </div>
    <h1>${areaMap.name()?html}</h1>
    <#if areaMap.levelDescription()?has_content>
        <div class="subheading">${areaMap.levelDescription()?html} &mdash; ${areaMap.author()?html}</div>
    </#if>
</div>
<div class="fragments">
    <#list areaMap.fragments as fragment>
        <div class="fragment">
            <div style="grid-template-columns: repeat(${fragment.columns()}, 1fr);" class="cells">
                <#list fragment.grid() as row>
                    <#list row as cell>
                        <#if cell.type == "RoomCell">
                            <!-- Room vnum ${cell.room.vnum?string["0"]} at (${cell.position.x}, ${cell.position.y}) -->
                            <div class="${styleCell} ${styleRoomCell} ${styleSector}-${cell.room.sectorType.tag}" <#if cell.label?has_content>id="label-${cell.label?html}"</#if>>
                                <#if cell.hasNorthEdge><div class="${styleCellEdge}-n ${styleCellEdgeType}-${cell.northEdge.style}"></div></#if>
                                <#if cell.hasSouthEdge><div class="${styleCellEdge}-s ${styleCellEdgeType}-${cell.southEdge.style}"></div></#if>
                                <#if cell.hasEastEdge><div class="${styleCellEdge}-e ${styleCellEdgeType}-${cell.eastEdge.style}"></div></#if>
                                <#if cell.hasWestEdge><div class="${styleCellEdge}-w ${styleCellEdgeType}-${cell.westEdge.style}"></div></#if>
                                <#list cell.links as direction, link>
                                    <#if options.renderLinkedExitSymbols && link.state == "LINKED">
                                        <div class="${styleCellLink} ${styleCellLink}-${direction.tag}">
                                            <span class="${styleCellLinkTypeLink}">+</span>
                                        </div>
                                    </#if>
                                    <#if link.state == "UNLINKED">
                                        <div class="${styleCellLink} ${styleCellLink}-${direction.tag}">
                                            <span class="${styleCellLinkTypeUnlinked}">?</span>
                                        </div>
                                    </#if>
                                    <#if link.state == "JUMP">
                                        <div class="${styleCellLink} ${styleCellLink}-${direction.tag}">
                                            <a href="#label-${link.targetLabel?html}"><span class="${styleCellLinkTypeJump} ${styleArrowDirection}-${direction.tag}" title="${link.description?html}">${link.targetLabel?html}</span></a>
                                        </div>
                                    </#if>
                                </#list>
                                <div class="${styleRoomMain}">
                                    <div class="${styleCellMainContent}">
                                        <#if options.renderVnums><div class="vnum">#${cell.room.vnum?string["0"]}</div></#if>
                                        <#if options.renderPositions><div>${cell.position}</div></#if>
                                        <div title="${cell.room.cleanName?html}">
                                            <#if cell.label?has_content>
                                                <div><span class="${styleRoomLabel}">${cell.label}</span></div>
                                            </#if>
                                            <span>${cell.room.cleanName?truncate(30, "...")?html}</span>
                                        </div>
                                        <#if cell.flags?has_content>
                                            <div class="room-flags">
                                                <#list cell.flags as flag>
                                                    <span class="${styleRoomFlag} ${styleRoomFlag}-${flag.style}" title="${flag.description?html}"></span>
                                                </#list>
                                            </div>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        <#elseif cell.type == "AreaExitCell">
                            <div class="${styleCell} area-exit-cell" <#if cell.label?has_content>id="label-${cell.label?html}"</#if>>
                                <div class="${styleCellEdge}-n ${styleCellEdgeType}-${cell.northEdge.style}"></div>
                                <div class="${styleCellEdge}-s ${styleCellEdgeType}-${cell.southEdge.style}"></div>
                                <div class="${styleCellEdge}-e ${styleCellEdgeType}-${cell.eastEdge.style}"></div>
                                <div class="${styleCellEdge}-w ${styleCellEdgeType}-${cell.westEdge.style}"></div>
                                <#list cell.links as direction, link>
                                    <div class="${styleCellLink} ${styleCellLink}-${direction.tag}">
                                        <#if options.renderLinkedExitSymbols && link.state == "LINKED">
                                            <span class="${styleCellLinkTypeLink}">+</span>
                                        </#if>
                                        <#if link.state == "UNLINKED">
                                            <span class="${styleCellLinkTypeUnlinked}">?</span>
                                        </#if>
                                        <#if link.state == "JUMP">
                                            <a href="#label-${link.targetLabel?html}"><span class="${styleCellLinkTypeJump} ${styleArrowDirection}-${direction.tag}" title="${link.description?html}">${link.targetLabel?html}</span></a>
                                        </#if>
                                    </div>
                                </#list>
                                <div class="area-exit-main">
                                    <div class="${styleCellMainContent}">
                                        <#if cell.label?has_content>
                                            <div><span class="${styleRoomLabel}">${cell.label}</span></div>
                                        </#if>
                                        <span>To <a href="${cell.mapReference?html}">${cell.area.name?truncate(35, "...")?html}</a></span>
                                    </div>
                                </div>
                            </div>
                        <#elseif cell.type == "NorthSouthConnectorCell">
                            <div class="${styleCell} ${styleConnectorCell}">
                                <div class="${styleNorthSouthConnector}"></div>
                            </div>
                        <#elseif cell.type == "EastWestConnectorCell">
                            <div class="${styleCell} ${styleConnectorCell}">
                                <div class="${styleEastWestConnector}"></div>
                            </div>
                        <#else>
                            <div class="${styleCell} ${styleEmptyCell}"></div>
                        </#if>
                    </#list>
                </#list>
            </div>
        </div>
    </#list>
    <div id="legend" class="legend">
        <div class="${styleLegendSymbol}"><span class="${styleRoomLabel}">123</span></div>
        <div class="${styleLegendDescription}">Numbered location</div>

        <div class="${styleLegendSymbol}"><span class="${styleCellLink} ${styleCellLinkTypeJump} ${styleArrowDirection}-u">123</span></div>
        <div class="${styleLegendDescription}">Exit to a numbered location</div>

        <div class="${styleLegendSymbol}"><span class="${styleRoomFlag} ${styleRoomFlag}-randomized-exits"></span></div>
        <div class="${styleLegendDescription}">Randomized exits</div>

        <div class="${styleLegendSymbol}"><span class="${styleRoomFlag} ${styleRoomFlag}-no-mobiles"></span></div>
        <div class="${styleLegendDescription}">Mobiles cannot enter</div>

        <div class="${styleLegendSymbol}"><span class="${styleRoomFlag} ${styleRoomFlag}-no-recall"></span></div>
        <div class="${styleLegendDescription}">Cannot recall</div>

        <div class="${styleLegendSymbol}"><span class="${styleRoomFlag} ${styleRoomFlag}-healer"></span></div>
        <div class="${styleLegendDescription}">Healer</div>

        <div class="${styleLegendSymbol}"><span class="${styleRoomFlag} ${styleRoomFlag}-shop"></span></div>
        <div class="${styleLegendDescription}">Shop</div>

        <div class="${styleLegendSymbol}"><span class="${styleRoomFlag} ${styleRoomFlag}-teacher"></span></div>
        <div class="${styleLegendDescription}">Teacher</div>

        <div class="${styleLegendSymbol}"><span class="area-exit-cell"></span></div>
        <div class="${styleLegendDescription}">Exit to another area</div>

        <div class="${styleLegendSymbol}"><span class="${styleCellLink} ${styleCellLinkTypeUnlinked}">?</span></div>
        <div class="${styleLegendDescription}">Unknown exit</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-inside"></span></div>
        <div class="${styleLegendDescription}">Inside</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-city"></span></div>
        <div class="${styleLegendDescription}">City</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-field"></span></div>
        <div class="${styleLegendDescription}">Fields</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-forest"></span></div>
        <div class="${styleLegendDescription}">Forest</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-hills"></span></div>
        <div class="${styleLegendDescription}">Hills</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-mountain"></span></div>
        <div class="${styleLegendDescription}">Mountains</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-water_swim"></span></div>
        <div class="${styleLegendDescription}">Swimmable water</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-water_no_swim"></span></div>
        <div class="${styleLegendDescription}">Unswimmable water</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-underwater"></span></div>
        <div class="${styleLegendDescription}">Underwater</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-underwater_ground"></span></div>
        <div class="${styleLegendDescription}">Underwater ground</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-air"></span></div>
        <div class="${styleLegendDescription}">In the air</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-desert"></span></div>
        <div class="${styleLegendDescription}">Desert</div>

        <div class="${styleLegendSymbol}"><span class="${styleLegendSector} ${styleSector}-swamp"></span></div>
        <div class="${styleLegendDescription}">Swamp</div>
    </div>
</div>
</body>
</html>
