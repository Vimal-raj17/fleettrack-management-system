package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CompliancePermitEntity
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        InsuranceRecordEntity::class,
        FitnessCertificateEntity::class,
        CompliancePermitEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FleetDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun insuranceDao(): InsuranceDao
    abstract fun fitnessDao(): FitnessDao
    abstract fun complianceDao(): ComplianceDao

    companion object {
        @Volatile
        private var INSTANCE: FleetDatabase? = null

        fun getDatabase(context: Context): FleetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FleetDatabase::class.java,
                    "fleettrack_master.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
