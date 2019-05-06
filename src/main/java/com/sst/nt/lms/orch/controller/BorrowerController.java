package com.sst.nt.lms.orch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sst.nt.lms.orch.model.Book;
import com.sst.nt.lms.orch.model.Borrower;
import com.sst.nt.lms.orch.model.Branch;
import com.sst.nt.lms.orch.model.BranchCopies;
import com.sst.nt.lms.orch.model.Loan;

@RestController
public class BorrowerController {
	
	private static String borrowerUrl = "borrower-service";
	
	@Autowired
	RestTemplate restTemplate;
	
	/**
	 * Helper method to reduce the amount of repetitive code required for "get-all"
	 * methods.
	 * @param url the URL to send the REST request to
	 * @param <T> the type we expect
	 * @return the response the server sent
	 */
	private <T> ResponseEntity<T> methodCall(final String url, final HttpMethod method) {
		return restTemplate.exchange(url, method, null,
				new ParameterizedTypeReference<T>() {});
	}

	/**
	 * Allows a borrower to borrow a book from a branch and lets the client know of
	 * the status.
	 *
	 * @param cardNo   id for borrower
	 * @param branchId id for branch
	 * @param bookId   id for book
	 * @return Loans if created correctly with an appropriate http code, else an
	 *         appropriate http error code
	 */
	@PostMapping(path = "/borrowers/{cardNo}/branches/{branchId}/books/{bookId}")
	public ResponseEntity<Loan> borrowBook(@PathVariable("cardNo") final int cardNo,
			@PathVariable("branchId") final int branchId,
			@PathVariable("bookId") final int bookId) {
		return this.<Loan>methodCall("http://" + borrowerUrl + "/borrowers/" + cardNo + "/branches/" + branchId + "/books/" + bookId, HttpMethod.POST);
	}

	/**
	 * To retrieve a list of book copies of a particular branch, the client must
	 * supply a branch id, which the server will use to get the associated branch
	 * entity, which is used to fetch the list of book copies of the requested
	 * branch.
	 *
	 * @param branchId used to get a list of book copies associated with the given
	 *                 branchId (branch)
	 * @return a list of book copies associated with the given branch Id if the
	 *         branch associated to the branch id exists
	 * @throws TransactionException A retrieval exception will be thrown if the
	 *                              branch associated to the branch id given does
	 *                              not exist or if the search for the book copies
	 *                              list failed.
	 */
	@GetMapping(path = "/branches/{branchId}/copies")
	public ResponseEntity<List<BranchCopies>> getAllBranchCopies(
			@PathVariable("branchId") final int branchId) {
		return this.<List<BranchCopies>>methodCall("http://" + borrowerUrl + "/branches/" + branchId + "/copies", HttpMethod.GET);
	}

	/**
	 * For client who would like to return a book.
	 *
	 * @param cardNo   id for a particular borrower
	 * @param branchId id for a particular branch
	 * @param bookId   id for a particular book
	 * @return Success message with 204(NO_CONTENT) code if the book was returned
	 *         correctly, 404(NOT_FOUND) if the entry of the given cardNo, branchId,
	 *         and bookId does not exist, 409(CONFLICT) if the book is overdue, or
	 *         returns a 500(INTERNAL_SERVER_ERROR) if the roll back fails
	 * @throws TransactionException Throws an UnknownSQLException if something goes
	 *                              wrong with the book copies and throws a
	 *                              DeleteException if something goes wrong with
	 *                              deleting the entry
	 */
	// FIXME: This should have 'loan' somewhere in the path!
	@DeleteMapping(path = "/borrowers/{cardNo}/branches/{branchId}/books/{bookId}")
	public ResponseEntity<String> returnBook(
			@PathVariable("cardNo") final int cardNo,
			@PathVariable("branchId") final int branchId,
			@PathVariable("bookId") final int bookId) {
		String returningBookUrl = "http://" + borrowerUrl + "/borrowers/" + cardNo + "/branches/" + branchId + "/books/" + bookId;
		
		return this.<String>methodCall(returningBookUrl, HttpMethod.DELETE);
	}

	/**
	 * Get all branches from which the borrower has an outstanding book loan.
	 *
	 * @param cardNo id for a particular borrower
	 * @return 200(OK) if the borrower exists in the database and if everything goes
	 *         correctly or will return 500(an internal server error) the roll back
	 *         fails
	 * @throws TransactionException retrieve exception if it cannot find the given
	 *                              borrower
	 */
	@GetMapping(path = "/borrowers/{cardNo}/branches") // FIXME: Should somehow indicate this is branches *with an outstanding loan* ...
	public ResponseEntity<List<Branch>> getAllBranchesWithLoan(
			@PathVariable("cardNo") final int cardNo) {
		String getAllBranchesUrl = "http://" + borrowerUrl + "/borrowers/" + cardNo + "/branches";
		return this.<List<Branch>>methodCall(getAllBranchesUrl, HttpMethod.GET);
	}

	/**
	 * Get all book loans the borrower has borrowed from any library branch.
	 *
	 * @param cardNo id for a particular borrower
	 * @return 200(OK) if the borrower exists in the database and if everything goes
	 *         correctly or will return 500(an internal server error) the roll back
	 *         fails
	 * @throws TransactionException retrieve exception if it cannot find the given
	 *                              borrower
	 */
	@GetMapping(path = "/borrowers/{cardNo}/loans")
	public ResponseEntity<List<Loan>> getAllBorrowedBooks(
			@PathVariable("cardNo") final int cardNo) {
		String getAllBorrowerLoansUrl = "http://" + borrowerUrl + "/borrowers/" + cardNo + "/loans";
		return this.<List<Loan>>methodCall(getAllBorrowerLoansUrl, HttpMethod.GET);
	}

	/**
	 * Give the client a borrower with a given card number.
	 *
	 * @param cardNo id for a particular borrower
	 * @return a ResponseEntity of a borrower with an ok code or will return 500(an
	 *         internal server error) the roll back fails
	 * @throws TransactionException retrieve exception if it cannot find the
	 *                              requested borrower
	 */
//	@GetMapping(path = "/borrowers/{cardNo}") Already exists in executive admin controller
	public ResponseEntity<Borrower> getBorrowerById(
			@PathVariable("cardNo") final int cardNo) {
		return this.<Borrower>methodCall("http://" + borrowerUrl + "/borrowers/" + cardNo, HttpMethod.GET);
	}

	/**
	 * Give the client a branch with a given branchId.
	 *
	 * @param branchId id for a particular branch
	 * @return a ResponseEntity of a branch with an 200(OK) code or will return
	 *         500(an internal server error) the roll back fails
	 * @throws TransactionException retrieve exception if it cannot find the
	 *                              requested branch
	 */
//	@GetMapping(path = "/branches/{branchId}") Already exists in executive admin controller
	public ResponseEntity<Branch> getbranch(
			@PathVariable("branchId") final int branchId) {
		return this.<Branch>methodCall("http://" + borrowerUrl + "/branches/" + branchId, HttpMethod.GET);
	}

	/**
	 * Give the client a book with a given bookId.
	 *
	 * @param bookId id for a particular branch
	 * @return a ResponseEntity of a book with an 200(OK) code or will return 500(an
	 *         internal server error) the roll back fails
	 * @throws TransactionException retrieve exception if it cannot find the
	 *                              requested book
	 */
//	@GetMapping(path = "/books/{bookId}") Already exists in catalog admin controller
	public ResponseEntity<Book> getBook(@PathVariable("bookId") final int bookId) {
		return this.<Book>methodCall("http://" + borrowerUrl + "/books/" + bookId, HttpMethod.GET);
	}

	/**
	 * Gives client a loan object based on URI given by client.
	 *
	 * @param cardNo   id for a borrower
	 * @param branchId id for a branch
	 * @param bookId   id for a book
	 * @return a ResponseEntity of a loan with an ok code or an appropriate http
	 *         error code
	 * @throws TransactionException send an internal server error code if rollback
	 *                              fails, else sends a not found code
	 */
	@GetMapping(path = "/borrowers/{cardNo}/branches/{branchId}/books/{bookId}")
	public ResponseEntity<Loan> getLoanByIds(
			@PathVariable("cardNo") final int cardNo,
			@PathVariable("branchId") final int branchId,
			@PathVariable("bookId") final int bookId) {
		String url = "http://" + borrowerUrl + "/borrowers/" + cardNo + "/branches/" + branchId + "/books/" + bookId;
		ResponseEntity<Loan> returnValue = this.<Loan>methodCall(url, HttpMethod.GET);
		return returnValue;
	}

	/**
	 * Gives client a list of all branches.
	 *
	 * @return a list of all branches
	 * @throws TransactionException if something goes wrong with the execution of
	 *                              the query (throws a criticalError)
	 */
	// TODO: Uncomment once controllers are split for service-discovery refactoring
//	@GetMapping(path = "/branches") conflicts with anther controller on this orchestrator
	public ResponseEntity<List<Branch>> getAllBranches() {
		String getAllBranchesUrl = "http://" + borrowerUrl + "/branches";
		return this.<List<Branch>>methodCall(getAllBranchesUrl, HttpMethod.GET);
	}
}
