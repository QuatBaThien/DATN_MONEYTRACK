import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.model.data.Subject
import com.thienhd.noteapp.databinding.SubjectItemViewBinding

class SubjectItemAdapter(
    var subjectList: List<Subject>,
) : RecyclerView.Adapter<SubjectItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SubjectItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SubjectItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(subjectList[position]){
                binding.tvSubjectName.text = this.name
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return subjectList.size
    }
}
