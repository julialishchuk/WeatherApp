package com.plcoding.weatherapp.data.mappers

import com.plcoding.weatherapp.data.remote.WeatherDataDto
import com.plcoding.weatherapp.data.remote.WeatherDto
import com.plcoding.weatherapp.domain.weather.WeatherData
import com.plcoding.weatherapp.domain.weather.WeatherInfo
import com.plcoding.weatherapp.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.Duration


//private data class IndexedWeatherData(
//    val index: Int,
//    val data: WeatherData
//)


fun WeatherDataDto.toWeatherDataPerDayMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
        WeatherData(
            time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
            temperatureCelsius = temperature,
            pressure = pressure,
            windSpeed = windSpeed,
            humidity = humidity,
            weatherType = WeatherType.fromWMO(weatherCode)
        )
    }.groupBy {
        daysBetweenDateAndNow(it.time)
    }

}

private fun daysBetweenDateAndNow(dateTime: LocalDateTime): Int {
    val dateStartOfDay = dateTime.toLocalDate().atStartOfDay()
    val nowStartOfDay = LocalDate.now().atStartOfDay()
    //Log.d("111111111", "now = ${LocalDateTime.now()} , startNow = $startNow  start2=$start2");
    //Log.d("111111111", "days = $days");
    return Duration.between(nowStartOfDay, dateStartOfDay).toDays().toInt()
}

fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataPerDayMap()
    val hourNow = LocalDateTime.now().hour
    val currentWeather = weatherDataMap[0]?.find {
        it.time.hour == hourNow
    }
    return WeatherInfo(
        weatherDataMap,
        currentWeather
    )
}









