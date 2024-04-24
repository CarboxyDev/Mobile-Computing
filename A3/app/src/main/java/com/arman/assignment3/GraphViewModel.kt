import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.arman.assignment3.data.DatabaseRepository
import com.arman.assignment3.data.db.OrientationEntity
import kotlinx.coroutines.flow.Flow

class GraphViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseRepoStore = DatabaseRepository(
        context = getApplication(),
    )
    private val orientationDao = databaseRepoStore.getOrientationDao();

    val allOrientationData: Flow<List<OrientationEntity>> = orientationDao.getOrientationData();

}
