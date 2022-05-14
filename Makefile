.PHONY: default build clean clean-output parse check maps maps-unminified query-db
.DEFAULT_GOAL: build

GRADLE = ./gradlew
JAVA = java
# npm install html-minifier -g
HTML_MINIFIER = html-minifier --collapse-whitespace --remove-optional-tags --remove-redundant-attributes \
	--remove-script-type-attributes --remove-tag-whitespace --use-short-doctype --minify-css true

build: output/lib/area-parser.jar output/lib/area-query.jar output/lib/area-checker.jar output/lib/map-maker.jar

parse: output/data/areas.yml

query-db: output/data/query-db.json

check: output/lib/area-checker.jar output/data/areas.yml
	$(JAVA) -jar output/lib/area-checker.jar -i output/data/areas.yml

maps-unminified: output/lib/map-maker.jar output/data/areas.yml | output/maps/unminified
	$(JAVA) -jar output/lib/map-maker.jar -i output/data/areas.yml -o output/maps/unminified

maps: maps-unminified | output/maps/minified
	@for inputFile in output/maps/unminified/*.html ; do \
		echo Minifying $$(basename $$inputFile) ;\
	    $(HTML_MINIFIER) $$inputFile -o output/maps/minified/$$(basename $$inputFile) ;\
  	done

output/lib/%.jar: | output/lib
	@echo
	@echo Building $*...
	$(GRADLE) :$*:build
	@cp -v */build/libs/$*.jar output/lib/

output/data/areas.yml: output/lib/area-parser.jar sample-data/area/area.lst sample-data/area/*.are sample-data/area/area.lst sample-data/area/MOBProgs/* | output/data
	$(JAVA) -jar output/lib/area-parser.jar -i sample-data/area -a area.lst -o output/data/areas.yml

output/data/query-db.json: output/lib/area-query.jar output/data/areas.yml | output/data
	$(JAVA) -jar output/lib/area-query.jar -i output/data/areas.yml -o output/data/query-db.json

output/lib output/data output/maps output/maps/unminified output/maps/minified:
	@mkdir -p $@

clean: clean-output
	$(GRADLE) clean

clean-output:
	rm -rf output/
