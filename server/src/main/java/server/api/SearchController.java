package server.api;

import commons.NoteTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.SearchService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /** This method handles the GET request to the address bellow and passes the data
     * to the service that performs the search.
     *
     * @param keywords the words by which the search is performed
     * @param matchAll indicator whether the search should match all keywords or not
     * @param searchIn a setting to specify whether to search only in the title, only in the content or both of them.
     * @param collectionId the id of the collection within which the search is performed
     * @return list of all notes that match the search options
     */
    @PostMapping(path = "/")
    public ResponseEntity<List<NoteTitle>> searchNotes(@RequestBody String keywords,
                                                       @RequestParam("matchAll") String matchAll,
                                                       @RequestParam("searchIn") String searchIn,
                                                       @RequestParam(required=false, name="collectionId")
                                                           UUID collectionId) {

        List<NoteTitle> result = searchService.getSearchResults(collectionId, keywords,
                                                                Boolean.valueOf(matchAll), searchIn);

        return ResponseEntity.ok(result);
    }



}
