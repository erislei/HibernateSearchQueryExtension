package de.hotware.hibernate.query.intelligent;

@Queries(@Query(must = @Must(subQuery = "first")))
@SubQueries({
	@SubQuery(id = "first", 
		query =
			@Query(
				should = {
						@Should(@SearchField(fieldName = "toast", propertyName ="toast")), 
						@Should(@SearchField(fieldName = "toaster", propertyName="toaster"))
				}
			)
		)	
	}
)
public class Test {

}