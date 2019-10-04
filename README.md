MyelinFlow - it's a console tool to get the execution order of the population functions in Myelin_DB.
<br/><br/>

**Restrictions:**
- This tool is compatible only with datastaging and dw schemas. Graphql schema doesn't require manual execution since graphql is populating by dw triggers automatically.
- This tool analyzes only standard population functions having the pattern:

		insert into schema_name.table_name (
		...
		)
		select
		...
		from schema_name.view_name
	All other functions should be added to excluded functions in the configuration file. In another 	case, the result may be incorrect.
- This tool analyzes only functions with name having prefix *‘populate’*
<br/><br/>

**Requirements:** JRE 8 or higher. Download link: https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
<br/><br/>

**How to run it:**
1. Take the *myelinflow.jar* file
2. Configure your configuration file. See *config-example.properties* file for the example.
3. Run the command in the console:

		java -jar path/to/myelinflow.jar -config path/to/your/config-file.properties
4. You will get a *.xlsx* file in the result
<br/><br/>

**Configuration file description:**
See *config-example.properties* file for the example.

**db.host** - database host *(required)*<br/>
**db.port** - database port *(required)*<br/>
**db.name** - database name *(required)*<br/>
**db.user** - your database user name *(required)*<br/>
**db.password** - your database user password *(required)*<br/>

**result.file.path** - the path where you want to export the result file *(required)*<br/>
**result.file.name** - the name of the result file *(required)*

**schema.&lt;index>** - schema to analyze. Value is a single schema name. *&lt;index>* - is the schema population order relative to other schemas. For example:

	schema.0=datastaging
	schema.1=dw
*(required)*


**source.schemas.&lt;index>** - configure this property to get population order for specific raw schemas. Value is a single raw schema or list of raw schemas separated by ',' or ',\' to go to the next line. You can have any number of property source.schemas but with different indexes. *&lt;index>* -  is any integer value. For example:

	source.schemas.0=gisraw,ctrakraw
	source.schemas.1=nsuiteraw
*(optional)*

**exclude.functions.&lt;schema>** - excluded functions. Functions configured in this property will be excluded from analyzation and from the result order. Put here all the functions having different flow from typical *“insert into the table from the view”*. In another case, the result may be incorrect. Value is a list of function names separated by ',' or ',\' to go to the next line. *&lt;schema>* - is the schema name of the excluded functions. You can have one property *exclude.functions* for each schema. For example:

	exclude.functions.datastaging=\
		populateentity,\
		populateemployeegroupmap,\
		populateentitydeterminantactivesync,\
		populateentitymap

	exclude.functions.dw=\
		populatedimentity,\
		populatedimvendorgroup,\
		populatedimemployeegroup
*(optional)*
