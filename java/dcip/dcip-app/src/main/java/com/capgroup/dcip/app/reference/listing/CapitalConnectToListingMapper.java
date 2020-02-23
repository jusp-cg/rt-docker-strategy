package com.capgroup.dcip.app.reference.listing;

import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.capgroup.dcip.capitalconnect.company.Company;
import com.capgroup.dcip.capitalconnect.suggestion.SearchResult;
import com.capgroup.dcip.capitalconnect.suggestion.SearchResultItem;
import com.capgroup.dcip.capitalconnect.suggestion.SearchResultItems;

/**
 * Maps a CapitalConnect Company to a Listing 
 */
@Mapper
public interface CapitalConnectToListingMapper {

	default Stream<ListingModel> toListingModels(SearchResult result){
		SearchResultItems<Company> companies = result.getCompanies();
		if (companies == null || companies.getItems() == null) {
			return Stream.empty();
		}
		
		return toListingModels(companies.getItems().stream().map(SearchResultItem::getItem));
	}
	
	@Mappings({@Mapping(target="identifiers.ric", source="listingTradingReutersInstrumentCode"),
		@Mapping(target="identifiers.isin", source="instrumentIsinIdentifier"),
		@Mapping(target="identifiers.cusip", source="instrumentCusipIdentifier"),
		@Mapping(target="identifiers.sedol", source="listingSedolIdentifier"),
		@Mapping(target="identifiers.cgSymbol", source="listingCgSymbol")})
	ListingModel toListingModel(Company searchItem);
		
	Stream<ListingModel> toListingModels(Stream<Company> searchItems);
}
