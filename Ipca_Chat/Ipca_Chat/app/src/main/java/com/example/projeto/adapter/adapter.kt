package example.javatpoint.com.kotlintablayoutexample

import android.content.Context;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.projeto.ui.conversas.ConversasFragment
import com.example.projeto.ui.grupos.GruposFragment
import com.example.projeto.ui.home.HomeFragment

class adapter(fm:FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return ConversasFragment()
            }
            1 -> {
                return GruposFragment()
            }
            else -> {
                return ConversasFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Conversas"
            }
            1 -> {
                return "Grupos"
            }

        }
        return super.getPageTitle(position)
    }
}